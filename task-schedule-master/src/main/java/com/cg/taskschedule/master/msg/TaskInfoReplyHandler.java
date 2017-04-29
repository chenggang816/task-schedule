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
		//表示客户端任务是否是最新的，true表示是最新的
		boolean isUpdated = true;
		//将任务更新状态信息写入到文件中
		String ip = msg.get("ip").toString();
		String port = msg.get("port").toString();
		JSONObject mapTaskNameInfo = (JSONObject)msg.get("content");
		
		System.out.println("任务更新信息：");
		System.out.println(JSONHelper.toJSONString(msg));
		
		//将任务版本信息写入文件
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
		//需要更新，返回false
		return String.valueOf(isUpdated);
	}

//	private String getLocalTaskVersion(String taskName) {
//		return null;
//	}


}
