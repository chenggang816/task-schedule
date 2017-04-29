package com.cg.taskschedule.slave.msg;

import java.io.IOException;
import java.io.StringWriter;

import org.json.simple.JSONObject;

import com.cg.common.util.JSONHelper;
import com.cg.taskschedule.slave.Server;

public class MsgCreator {
	private static String[] keys = {"ip","port","type","content"};
	private static String createMsg(Object[] values){
		if(keys.length < values.length) throw new RuntimeException("值数组长度越界");
		JSONObject obj = new JSONObject();
		obj.put(keys[0], HelloMsgHandler.ip);
		obj.put(keys[1], Server.port);
		for(int i = 0; i < values.length; i++){
			obj.put(keys[i + 2], values[i]);
		}
		return JSONHelper.toJSONString(obj);
	}
	
	public static String createReplyMsg(){
		JSONObject obj = new JSONObject();
		obj.put("type", "REPLY");
		StringWriter out = new StringWriter();
		try {
			obj.writeJSONString(out);
			return out.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String createTaskInfoReplyMsg(JSONObject taskState){
		return createMsg(new Object[]{"TASK_INFO_REPLY",taskState});
	}
}
