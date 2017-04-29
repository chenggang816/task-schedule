package com.cg.taskschedule.slave.msg;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.cg.common.util.JSONHelper;

public class MsgHandlerFactory {
	public static MsgHandler getMsgHandler(String strMsg){
		JSONObject msg = JSONHelper.parse(strMsg);
		try {
			String msgType = msg.get("type").toString();
			switch(msgType){
			case "HELLO":
				return new HelloMsgHandler(msg.get("ip").toString());
			case "TASK_INFO":
				return new TaskInfoMsgHandler(msg.get("content").toString());
			case "TASK_UPDATE":
				return new TaskUpdateMsgHandler(msg);
			case "TASK_CLEAR":
				return new TaskClearMsgHandler(msg);
			case "TASK_EXECUTE":
				return new TaskExecutionMsgHandler(msg.get("content").toString());
			default:
				return new UnresolvedMsgHandler();
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			return new UnresolvedMsgHandler();
		}
	}
}
