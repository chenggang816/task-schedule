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
	final DefaultTableModel model = new DefaultTableModel(new String[]{"序号","IP","端口号","主机名","服务是否开启","任务更新"},0){
		public boolean isCellEditable(int r,int c){
			return true;
		}
	};
	/**
	 * 初始化块，初始化一些公共操作
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
			model.addRow(new Object[]{i + 1,ipp.getIp(),ipp.getPort(),"未知","未知","未知"});
		}
	}
	
	/**
	 * 更新tasks.json文件，这是一个临时文件，存放全部任务信息
	 */
	public void updateTasksJsonFile(){
		File fileTasks = FileMgr.getTaskAllJsonFile();
		String tasksJSONString = JSONMgr.getTasksJsonStr();
		FileHelper.WriteToFile(fileTasks, tasksJSONString);
	}
	
	/**
	 * 获取所有ip
	 */
	public void doGainAllIp(){
		ipPortPairList.clear();
		ipPortPairList.add(new IpPortPair(NetHelper.getLocalHostIp()));
		ipPortPairList.addAll(IpPortPair.toIpPortList(NetHelper.getIPs()));
		Collections.sort(ipPortPairList);
		updateModel();
	}
	
	
	/**
	 * 获取主机名
	 */
	public void doGainHostNames() throws InterruptedException{
		Map<String, String> mapIpHostNames = NetHelper.getHostnames(IpPortPair.toIpList(ipPortPairList));
		for(int i=0;i<ipPortPairList.size();i++){
			String ip = ipPortPairList.get(i).getIp();
			model.setValueAt(mapIpHostNames.get(ip), i, 3);
		}
	}
	
	/**
	 * 测试服务是否开启
	 */
	public void doTestService(){
		mapIppClient = SocketHelper.tryCommunicate(ipPortPairList);
		for(int i = 0;i < ipPortPairList.size();i++){
			String r = mapIppClient.get(ipPortPairList.get(i)) == null ? "否" : "是";
			model.setValueAt(r, i, 4);
		}
	}
	
	/**
	 * 检查任务更新
	 */
	public void doCheckTaskUpdate(){
		if(ipPortPairList.isEmpty()) return;
		boolean[] b = checkUpdateState(ipPortPairList);
		for(int i = 0; i < ipPortPairList.size(); i++){
			boolean serviceOpen = model.getValueAt(i, 4).toString().equals("是");
			model.setValueAt(serviceOpen?(b[i]?"已最新":"需要更新"):"无服务", i, 5);
		}
	}
	
	/**
	 * 检查每一个主机上的任务是否是最新的，若是，对应states项为true
	 */
	private boolean[] checkUpdateState(final List<IpPortPair> ippList){
		boolean[] states = new boolean[ippList.size()];
		//更新文件
		updateTasksJsonFile();
		String tasksStr = FileHelper.ReadAllFromFile(FileMgr.getTaskAllJsonFile());
		if(tasksStr == null) throw new RuntimeException("无法获取本地任务信息");
		for(int i = 0; i < ippList.size(); i++){
			IpPortPair ipp = ippList.get(i);
			//将taskStr发送到各个Server主机，由各个主机返回任务更新状态
			if(mapIppClient == null){
				System.out.println("服务开启状态未知，请先获取服务开启状态");
				return states;
			}
			Client client = mapIppClient.get(ipp);
			if(client != null){
				//将全部任务的列表发给worker端，worker端应返回任务的版本信息
				String taskInfoReply = client.send(MsgCreator.createTaskInfoMsg(tasksStr));
				//对返回的信息进行处理，将任务版本信息写入data/client文件夹内的文件里，同时返回是否需要更新的信息
				MsgHandler handler = MsgHandlerFactory.getMsgHandler(taskInfoReply);
				states[i] = Boolean.parseBoolean(handler.handle());
			}
		}
		return states;
	}
	
	/**
	 * 任务更新
	 */
	public void doTaskUpdate(){
		if(ipPortPairList.size() > 0 && model.getValueAt(0, 5).toString().equals("未知")){
			doCheckTaskUpdate();
		}
		//获取需要更新的主机Ip与Port，读取文件内容，找出需要更新的任务，与Worker端通信进行更新
		for(int i = 0; i < ipPortPairList.size(); i++){
			if(model.getValueAt(i, 5).toString().equals("需要更新")){
				IpPortPair ipp = ipPortPairList.get(i); //需要更新的ip port
				Client client = mapIppClient.get(ipp);
				
				File taskInfoFile = FileMgr.getClientFile(ipp.getIp(), ipp.getPort());
				if(taskInfoFile == null) continue; //file为client文件下的文件
				
				JSONObject obj = JSONHelper.parse(FileHelper.ReadAllFromFile(taskInfoFile));
				if(obj == null) continue; //obj为文件内的字符串解析出来的json对象
				for(Object o:obj.keySet()){ 
					String taskName = (String)o;
					JSONObject taskDetail = (JSONObject)obj.get(o);
					String workerVersion = (String)taskDetail.get("version-worker");
					String managerVersion = (String)taskDetail.get("version-manager");
					if(Task.VersionCompare(managerVersion, workerVersion) != 0){
						//开始更新任务，遍历任务文件夹，每找到一个文件或一个空文件夹，向worker发送一次信息
						File theTaskDir = new File(FileMgr.getTaskDir(),taskName);
						clearTask(client,theTaskDir);
						updateTask(client, theTaskDir);
					}
				}
//				model.setValueAt("已最新",i, 5);
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
				//将要发送的文件名称、文件相对路径发送给worker(考虑空文件夹的情况)
				String filePath = file.getAbsolutePath();
				String fileRelativePath = filePath.substring(taskPath.length());
				String strMsg = MsgCreator.createTaskUpdateMsg(fileRelativePath,false);
				client.send(strMsg,false);
				//传输文件给worker
				client.sendFile(file);
			}else if(file.isDirectory()){
				updateTask(client, file);		//对dir下所有的文件夹递归调用此方法
			}
		}
		doCheckTaskUpdate(); //更新完之后再检查一遍，同时更新表格内容
	}
	
	/**
	 * 从配置文件中加载主机
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
	 * 编辑配置文件
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
	 * 查看配置文件
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
