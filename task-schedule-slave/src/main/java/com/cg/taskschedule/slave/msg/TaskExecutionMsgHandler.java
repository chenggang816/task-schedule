package com.cg.taskschedule.slave.msg;

import com.cg.taskschedule.service.Task;
import com.cg.taskschedule.slave.FileMgr;

public class TaskExecutionMsgHandler extends MsgHandler {

	String taskName;
	public TaskExecutionMsgHandler(String taskName) {
		this.taskName = taskName;
	}
	@Override
	public String handle() {
		//Ö´ÐÐÈÎÎñ
		Task task = new Task();
		task.fillTaskInfo(FileMgr.getTheTaskConfigFile(taskName));
		task.execute();
		return null;
	}

}
