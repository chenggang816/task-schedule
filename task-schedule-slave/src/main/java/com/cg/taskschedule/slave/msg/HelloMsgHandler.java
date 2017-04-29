package com.cg.taskschedule.slave.msg;

public class HelloMsgHandler extends MsgHandler{
	public static String ip;
	
	public HelloMsgHandler(String ip) {
		HelloMsgHandler.ip = ip;
	}
	
	@Override
	public String handle() {
		return MsgCreator.createReplyMsg();
	}

}
