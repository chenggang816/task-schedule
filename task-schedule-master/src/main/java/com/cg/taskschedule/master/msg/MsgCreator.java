package com.cg.taskschedule.master.msg;

import static com.cg.common.util.JSONHelper.toJSONString;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.json.simple.JSONObject;

import com.cg.common.util.JSONHelper;

public class MsgCreator {
	private static String[] keys = {"type","content","ip"}; //ip为worker端ip,防止worker有多个ip地址
	private static String createMsg(Object[] values){
		if(keys.length < values.length) throw new RuntimeException("值数组长度越界");
		JSONObject obj = new JSONObject();
		for(int i = 0; i < values.length; i++){
			obj.put(keys[i], values[i]);
		}
		return JSONHelper.toJSONString(obj);
	}
	/*
	 * 创建任务信息消息，任务信息为TaskAll.json的全部内容
	 */
	public static String createTaskInfoMsg(String taskInfoStr){
		return createMsg(new Object[]{"TASK_INFO", taskInfoStr});
	}

	/*
	 * 创建Hello消息的Json字符串
	 */
	public static String createHelloMsg(String ip){
		return createMsg(new Object[]{"HELLO",null,ip});
	}

	public static String createTaskUpdateMsg(String relativePath, boolean isDir){
		JSONObject content = new JSONObject();
		content.put("isDir", isDir);
		content.put("path", relativePath);
		return createMsg(new Object[]{"TASK_UPDATE",content});
	}
	public static String createTaskClearMsg(File theTaskDir) {
		return createMsg(new Object[]{"TASK_CLEAR",theTaskDir.getName()});
	}
	public static String createTaskExecuteMsg(String task) {
//		JSONObject content = new JSONObject();
//		content.put("ip", ip);
//		content.put("port", port);
//		content.put("task", task);
		return createMsg(new Object[]{"TASK_EXECUTE",task});
	}
}
