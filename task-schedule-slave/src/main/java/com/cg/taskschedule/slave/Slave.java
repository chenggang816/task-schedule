package com.cg.taskschedule.slave;

import java.io.IOException;

import com.cg.common.util.ExecuteHelper;
import com.cg.taskschedule.service.Task;

public class Slave {
	public static void main(String args[]){
//		Task task = new Task();
//		task.setTaskDir("D:\\gitrepository\\task-schedule\\task-schedule-slave\\data/task/task1");
//		task.setPath("task.jar");
//		task.execute();
		
//		ExecuteHelper.Execute2("data\\task\\task1\\task.jar",false);
		
//		try {
//			java.awt.Desktop.getDesktop().open(new java.io.File("data\\task\\task1\\task.jar"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		try {
			Runtime.getRuntime().exec("cmd /c start java -jar " + "data\\task\\task1\\task.jar");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
//		new Server().start();
		System.out.println("Server has exited.");
	}

}
