package com.cg.taskschedule.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.cg.common.util.FileHelper;

public class TaskMgr {
	/**
	 * 获取tasksDir文件夹下的所有任务，返回任务列表
	 * @param tasksDir
	 * @return
	 */
	public static List<Task> getTaskList(File tasksDir){
		List<Task> listTasks = new ArrayList<>();
		File[] taskFiles = tasksDir.listFiles();
		for(File taskFile:taskFiles){
			if(taskFile.isFile()) continue;
			
			File conf = new File(taskFile,"task.conf");
			Task task = new Task();
			task.parseJSON(FileHelper.ReadAllFromFile(conf));
			task.setName(taskFile.getName());
			listTasks.add(task);
		}
		return listTasks;
	}
	/**
	 * 获取任务列表，使用默认任务路径“data/task”
	 * @return
	 */
	public static List<Task> getTaskList(){
		return getTaskList(FileHelper.getDir("data/task"));
	}
}
