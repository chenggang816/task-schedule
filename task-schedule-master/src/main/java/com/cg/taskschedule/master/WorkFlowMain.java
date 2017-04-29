package com.cg.taskschedule.master;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.cg.common.util.FileHelper;
import com.cg.common.util.JSONHelper;
import com.cg.common.util.NetHelper;
import com.cg.taskschedule.master.msg.MsgCreator;
import com.cg.taskschedule.master.msg.MsgHandler;
import com.cg.taskschedule.master.msg.MsgHandlerFactory;
import com.cg.taskschedule.master.net.Client;
import com.cg.taskschedule.master.net.SocketHelper;
import com.cg.taskschedule.master.tools.FileMgr;
import com.cg.taskschedule.master.tools.JSONMgr;
import com.cg.taskschedule.service.Task;

public class WorkFlowMain {
	List<IpPortPair> ipPortPairList = new ArrayList<IpPortPair>();
	Map<IpPortPair, Client> mapIppClient;
	final DefaultTableModel model = new DefaultTableModel(new String[]{"���","IP","�˿ں�","������","�����Ƿ���","�������"},0){
		public boolean isCellEditable(int r,int c){
			return true;
		}
	};
	/**
	 * ��ʼ���飬��ʼ��һЩ��������
	 */
	{
		updateTasksJsonFile();
		doLoadHostsFromConfig();
		doTestService();
//		doCheckTaskUpdate();
	}
	
	private WorkFlowMain(){}
	private static WorkFlowMain workFlow;
	public static WorkFlowMain getWorkFlow(){
		if(workFlow == null)  workFlow = new WorkFlowMain();
		return workFlow;
	}
	
	public Map<IpPortPair, Client> getMapIppClient(){
		return mapIppClient;
	}
	
	public boolean canWork(){
		return ipPortPairList != null && ipPortPairList.size() > 0;
	}
	
	public TableModel getTabelModel(){
		return model;
	}

	
	private void updateModel(){
		model.setRowCount(0);
		for(int i=0;i<ipPortPairList.size();i++){
			IpPortPair ipp = ipPortPairList.get(i);
			model.addRow(new Object[]{i + 1,ipp.getIp(),ipp.getPort(),"δ֪","δ֪","δ֪"});
		}
	}
	
	/**
	 * ����tasks.json�ļ�������һ����ʱ�ļ������ȫ��������Ϣ
	 */
	public void updateTasksJsonFile(){
		File fileTasks = FileMgr.getTaskAllJsonFile();
		String tasksJSONString = JSONMgr.getTasksJsonStr();
		FileHelper.WriteToFile(fileTasks, tasksJSONString);
	}
	
	/**
	 * ��ȡ����ip
	 */
	public void doGainAllIp(){
		ipPortPairList.clear();
		ipPortPairList.add(new IpPortPair(NetHelper.getLocalHostIp()));
		ipPortPairList.addAll(IpPortPair.toIpPortList(NetHelper.getIPs()));
		Collections.sort(ipPortPairList);
		updateModel();
	}
	
	
	/**
	 * ��ȡ������
	 */
	public void doGainHostNames() throws InterruptedException{
		Map<String, String> mapIpHostNames = NetHelper.getHostnames(IpPortPair.toIpList(ipPortPairList));
		for(int i=0;i<ipPortPairList.size();i++){
			String ip = ipPortPairList.get(i).getIp();
			model.setValueAt(mapIpHostNames.get(ip), i, 3);
		}
	}
	
	/**
	 * ���Է����Ƿ���
	 */
	public void doTestService(){
		mapIppClient = SocketHelper.tryCommunicate(ipPortPairList);
		for(int i = 0;i < ipPortPairList.size();i++){
			String r = mapIppClient.get(ipPortPairList.get(i)) == null ? "��" : "��";
			model.setValueAt(r, i, 4);
		}
	}
	
	/**
	 * ����������
	 */
	public void doCheckTaskUpdate(){
		if(ipPortPairList.isEmpty()) return;
		boolean[] b = checkUpdateState(ipPortPairList);
		for(int i = 0; i < ipPortPairList.size(); i++){
			boolean serviceOpen = model.getValueAt(i, 4).toString().equals("��");
			model.setValueAt(serviceOpen?(b[i]?"������":"��Ҫ����"):"�޷���", i, 5);
		}
	}
	
	/**
	 * ���ÿһ�������ϵ������Ƿ������µģ����ǣ���Ӧstates��Ϊtrue
	 */
	private boolean[] checkUpdateState(final List<IpPortPair> ippList){
		boolean[] states = new boolean[ippList.size()];
		//�����ļ�
		updateTasksJsonFile();
		String tasksStr = FileHelper.ReadAllFromFile(FileMgr.getTaskAllJsonFile());
		if(tasksStr == null) throw new RuntimeException("�޷���ȡ����������Ϣ");
		for(int i = 0; i < ippList.size(); i++){
			IpPortPair ipp = ippList.get(i);
			//��taskStr���͵�����Server�������ɸ������������������״̬
			if(mapIppClient == null){
				System.out.println("������״̬δ֪�����Ȼ�ȡ������״̬");
				return states;
			}
			Client client = mapIppClient.get(ipp);
			if(client != null){
				//��ȫ��������б���worker�ˣ�worker��Ӧ��������İ汾��Ϣ
				String taskInfoReply = client.send(MsgCreator.createTaskInfoMsg(tasksStr));
				//�Է��ص���Ϣ���д���������汾��Ϣд��data/client�ļ����ڵ��ļ��ͬʱ�����Ƿ���Ҫ���µ���Ϣ
				MsgHandler handler = MsgHandlerFactory.getMsgHandler(taskInfoReply);
				states[i] = Boolean.parseBoolean(handler.handle());
			}
		}
		return states;
	}
	
	/**
	 * �������
	 */
	public void doTaskUpdate(){
		if(ipPortPairList.size() > 0 && model.getValueAt(0, 5).toString().equals("δ֪")){
			doCheckTaskUpdate();
		}
		//��ȡ��Ҫ���µ�����Ip��Port����ȡ�ļ����ݣ��ҳ���Ҫ���µ�������Worker��ͨ�Ž��и���
		for(int i = 0; i < ipPortPairList.size(); i++){
			if(model.getValueAt(i, 5).toString().equals("��Ҫ����")){
				IpPortPair ipp = ipPortPairList.get(i); //��Ҫ���µ�ip port
				Client client = mapIppClient.get(ipp);
				
				File taskInfoFile = FileMgr.getClientFile(ipp.getIp(), ipp.getPort());
				if(taskInfoFile == null) continue; //fileΪclient�ļ��µ��ļ�
				
				JSONObject obj = JSONHelper.parse(FileHelper.ReadAllFromFile(taskInfoFile));
				if(obj == null) continue; //objΪ�ļ��ڵ��ַ�������������json����
				for(Object o:obj.keySet()){ 
					String taskName = (String)o;
					JSONObject taskDetail = (JSONObject)obj.get(o);
					String workerVersion = (String)taskDetail.get("version-worker");
					String managerVersion = (String)taskDetail.get("version-manager");
					if(Task.VersionCompare(managerVersion, workerVersion) != 0){
						//��ʼ�������񣬱��������ļ��У�ÿ�ҵ�һ���ļ���һ�����ļ��У���worker����һ����Ϣ
						File theTaskDir = new File(FileMgr.getTaskDir(),taskName);
						clearTask(client,theTaskDir);
						updateTask(client, theTaskDir);
					}
				}
//				model.setValueAt("������",i, 5);
			}
		}
	}
	
	private void clearTask(Client client, File theTaskDir) {
		String strMsg = MsgCreator.createTaskClearMsg(theTaskDir);
		client.send(strMsg,false);
	}

	private void updateTask(Client client, File dir) {
		File[] files = dir.listFiles();
		String taskPath = FileMgr.getTaskDir().getAbsolutePath();
		if(files.length <= 0){
			String dirRelativePath = dir.getAbsolutePath().substring(taskPath.length());
			client.send(MsgCreator.createTaskUpdateMsg(dirRelativePath, true),false);
			return;
		}
		for(File file:files){
			if(file.isFile()){
				//��Ҫ���͵��ļ����ơ��ļ����·�����͸�worker(���ǿ��ļ��е����)
				String filePath = file.getAbsolutePath();
				String fileRelativePath = filePath.substring(taskPath.length());
				String strMsg = MsgCreator.createTaskUpdateMsg(fileRelativePath,false);
				client.send(strMsg,false);
				//�����ļ���worker
				client.sendFile(file);
			}else if(file.isDirectory()){
				updateTask(client, file);		//��dir�����е��ļ��еݹ���ô˷���
			}
		}
		doCheckTaskUpdate(); //������֮���ټ��һ�飬ͬʱ���±������
	}
	
	/**
	 * �������ļ��м�������
	 */
	public void doLoadHostsFromConfig(){
		String jsonStr = FileHelper.ReadAllFromFile(FileMgr.getConfigFile());
		if(jsonStr == null) return;
		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject)parser.parse(jsonStr);
			JSONObject configs = (JSONObject) json.get("configs");
			JSONArray hosts = (JSONArray)configs.get("hosts");
			ipPortPairList.clear();
			for(Object o:hosts){
				JSONObject host = (JSONObject)o;
				ipPortPairList.add(new IpPortPair(host.get("ip").toString(),((Long)host.get("port")).intValue()));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Collections.sort(ipPortPairList);
		updateModel();
	}
	
	/**
	 * �༭�����ļ�
	 */
	public void doEditConfigFile(){
		try {
			Runtime runtime=Runtime.getRuntime();  
			String[] commandArgs={"explorer.exe",FileMgr.getConfigFile().getAbsolutePath()};  
			Process process=runtime.exec(commandArgs);  
			int exitcode=process.waitFor();  
			System.out.println("finish:"+exitcode);  
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
	/**
	 * �鿴�����ļ�
	 */
	public void doViewConfigFile(){
		try {
			Runtime runtime=Runtime.getRuntime();  
			String[] commandArgs={"explorer.exe",FileMgr.getConfigDir().getAbsolutePath()};  
			Process process=runtime.exec(commandArgs);  
			int exitcode=process.waitFor();  
			System.out.println("finish:"+exitcode);  
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
}
