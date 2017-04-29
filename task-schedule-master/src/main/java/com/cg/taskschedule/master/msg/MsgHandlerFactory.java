package com.cg.taskschedule.master.msg;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MsgHandlerFactory {
	public static MsgHandler getMsgHandler(String strMsg){
		JSONParser parser = new JSONParser();
		JSONObject msg;
		try {
			msg = (JSONObject)parser.parse(strMsg);
			String msgType = msg.get("type").toString();
			switch(msgType){
			case "REPLY":
				return new ReplyMsgHandler();
			case "TASK_INFO_REPLY":
				return new TaskInfoReplyHandler(msg);
			default:
				return new UnresolvedMsgHandler();
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return new UnresolvedMsgHandler();
		}
	}
}
