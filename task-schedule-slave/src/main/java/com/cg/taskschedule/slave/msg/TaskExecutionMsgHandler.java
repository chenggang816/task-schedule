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
		//ִ������
//		Task task = new Task();
//		task.fillTaskInfo(FileMgr.getTheTaskConfigFile(taskName));
//		task.execute();
		
		Task task = new Task();
		task.setTaskDir("data/task/task1");
		task.setPath("task.jar");
		task.execute();
		return null;
	}

}
