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
		 * �ȴ����ļ��У�������ļ�Ҫ���䣬���ȡServerSocket���󣬽����ļ�
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
		 * �ļ�md5У�飺ͨ�������ֵ�Ƚϣ��ж��ļ��Ƿ���������(�˹����ݲ�ʵ��)
		 */
		return null;
	}

}
