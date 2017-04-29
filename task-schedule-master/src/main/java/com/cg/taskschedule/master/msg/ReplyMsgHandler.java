package com.cg.taskschedule.master.msg;

public class ReplyMsgHandler extends MsgHandler{

	@Override
	public String handle() {
		return null;//收到客户端发过来的REPLY消息之后，返回null表示不再发回消息
	}

}
