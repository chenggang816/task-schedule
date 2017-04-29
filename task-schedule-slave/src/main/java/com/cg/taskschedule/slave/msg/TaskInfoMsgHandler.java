package com.cg.taskschedule.slave.msg;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.cg.common.util.FileHelper;
import com.cg.common.util.JSONHelper;
import com.cg.taskschedule.slave.FileMgr;
import com.cg.taskschedule.service.Task;
import com.cg.taskschedule.slave.msg.MsgCreator;

public class TaskInfoMsgHandler extends MsgHandler {

	private String content;
	public TaskInfoMsgHandler(String content) {
		this.content = content;
	}
	/*
	 * 	��ȡ��������汾��Ϣ������JSON�ַ�����ʽ����
	 */
	@Override
	public String handle() {
		JSONObject obj = JSONHelper.parse(content);
		JSONArray tasks = (JSONArray)(obj.get("tasks"));
		Map<String, Object> mapTaskState = new HashMap<String, Object>();
		for(Object o:tasks){
			JSONObject task = (JSONObject)o;
			String taskName = (String)task.get("taskname");
			String version = (String)task.get("version");
			
			JSONObject taskInfo = new JSONObject();
			taskInfo.put("version-manager", version);
			mapTaskState.put(taskName, taskInfo);
			
			File taskConf = FileMgr.getTheTaskConfigFile(taskName);
			if(taskConf == null || !taskConf.exists()) {
				taskInfo.put("version-worker", "-1"); //��ʾ���񲻴���
				continue;
			}
			String taskConfStr = FileHelper.ReadAllFromFile(taskConf);
			if(taskConfStr == null){
				taskInfo.put("version-worker", "-2"); //��ʾ�����ļ�δ�ҵ�
				continue;
			}
			Task ti = new Task();
			ti.parseJSON(taskConfStr);
			taskInfo.put("version-worker", ti.getVersion());
			
		}
		JSONObject taskState = new JSONObject(mapTaskState);
		return MsgCreator.createTaskInfoReplyMsg(taskState);
	}

}
