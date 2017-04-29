package com.cg.taskschedule.master.msg;

public abstract class MsgHandler{
	/*
	 * 处理消息，返回需要反馈给客启端的消息，如果返回null，表示不需要反馈客户端
	 */
	public abstract String handle();
}
