package com.cg.taskschedule.slave.msg;

import java.io.File;

import org.json.simple.JSONObject;

import com.cg.common.util.FileHelper;
import com.cg.taskschedule.slave.FileMgr;
import com.cg.taskschedule.slave.Server;

public class TaskUpdateMsgHandler extends MsgHandler {
	JSONObject msg;
	public TaskUpdateMsgHandler(JSONObject msg) {
		this.msg = msg;
	}
	
	@Override
	public String handle() {
		/*
		 * 先创建文件夹，如果有文件要传输，则获取ServerSocket对象，接收文件
		 */
		JSONObject content = (JSONObject)msg.get("content");
		boolean isDir = (boolean)content.get("isDir");
		String path = (String)content.get("path");
		
		File taskDir = FileMgr.getTaskDir();
		if(isDir){
			new File(taskDir, path).mkdirs();
		}else{
//			Server.send(MsgCreator.createReplyMsg());
			Server.receiveFile(new File(taskDir,path));
		}
		/*
		 * 文件md5校验：通过与检验值比较，判断文件是否完整传输(此功能暂不实现)
		 */
		return null;
	}

}
