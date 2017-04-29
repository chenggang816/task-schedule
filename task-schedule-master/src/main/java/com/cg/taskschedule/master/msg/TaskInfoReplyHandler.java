package com.cg.taskschedule.master.msg;

import java.io.File;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.cg.common.util.FileHelper;
import com.cg.common.util.JSONHelper;
import com.cg.taskschedule.service.Task;
import com.cg.taskschedule.master.tools.FileMgr;

public class TaskInfoReplyHandler extends MsgHandler{
	private JSONObject msg;
	public TaskInfoReplyHandler(JSONObject msg) {
		this.msg = msg;
	}
	
	@Override
	public String handle() {
		if(msg == null) return "false";
		//��ʾ�ͻ��������Ƿ������µģ�true��ʾ�����µ�
		boolean isUpdated = true;
		//���������״̬��Ϣд�뵽�ļ���
		String ip = msg.get("ip").toString();
		String port = msg.get("port").toString();
		JSONObject mapTaskNameInfo = (JSONObject)msg.get("content");
		
		System.out.println("���������Ϣ��");
		System.out.println(JSONHelper.toJSONString(msg));
		
		//������汾��Ϣд���ļ�
		File file = FileMgr.getClientFile(ip,port,true);
		JSONHelper.saveJSONFile(mapTaskNameInfo, file);
		
		for(Object taskName:mapTaskNameInfo.keySet()){
			JSONObject taskDetail = (JSONObject)mapTaskNameInfo.get(taskName);
			String workerVersion = (String)taskDetail.get("version-worker");
			String managerVersion = (String)taskDetail.get("version-manager");
			if(workerVersion.equalsIgnoreCase("-1") || workerVersion.equalsIgnoreCase("-2")){
				isUpdated = false;
				break;
			}
			if(Task.VersionCompare(managerVersion, workerVersion) != 0){
				isUpdated = false;
				break;
			}
		}
		//��Ҫ���£�����false
		return String.valueOf(isUpdated);
	}

//	private String getLocalTaskVersion(String taskName) {
//		return null;
//	}


}
