package com.cg.taskschedule.master;

import java.io.IOException;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.cg.taskschedule.master.msg.MsgCreator;
import com.cg.taskschedule.master.net.Client;
import com.cg.taskschedule.service.Task;
import com.cg.taskschedule.service.TaskMgr;

public class WorkFlowTestTask {
	WorkFlowMain main = WorkFlowMain.getWorkFlow();
	Map<IpPortPair, Client> mapIppClient;
	private DefaultComboBoxModel<String> hostModel = new DefaultComboBoxModel<String>();
	private DefaultComboBoxModel<String> taskModel = new DefaultComboBoxModel<String>();
	public WorkFlowTestTask() {
		mapIppClient = main.getMapIppClient();
	}
	public ComboBoxModel<String> getComboboxModel(String category){
		switch(category){
		case "HOST":
			if(mapIppClient != null){
				for(IpPortPair ipp:mapIppClient.keySet()){
					if(mapIppClient.get(ipp) != null){
						hostModel.addElement(ipp.toString());
					}
				}
			}
			return hostModel;
		case "TASK":
			for(Task task:TaskMgr.getTaskList())
				taskModel.addElement(task.getName());
			return taskModel;
		default:
			return null;
		}
	}
	public void doExecuteTask() {
		String selHost = (String)hostModel.getSelectedItem();
		String selTask = (String)taskModel.getSelectedItem();
		if(selHost == null || selTask == null) {
			JOptionPane.showMessageDialog(null, "请选择主机和任务!");
			return;
		}
		String[] host = selHost.split(":");
		try {
			String ip = host[0];
			int port = Integer.parseInt(host[1]);
			Client client = new Client(ip, port);
			String msg = MsgCreator.createTaskExecuteMsg(selTask);
			client.send(msg,false);
		} catch (NumberFormatException | IOException e) {
			JOptionPane.showMessageDialog(null, "测试失败！错误消息：" + e.getMessage());
			e.printStackTrace();
		}
	}
}
