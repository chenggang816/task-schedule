package com.cg.taskschedule.master.tools;

import java.io.File;

import com.cg.common.util.FileHelper;

public class FileMgr {
	/**
	 * 获取data文件夹的File对象
	 */
	public static File getDataDir(){
		return FileHelper.getDir("data");
	}

	/**
	 * 获取tasks.json的File对象
	 */
	public static File getTaskAllJsonFile(){
		return FileHelper.getFile(getDataDir(),"TaskAll.json",true);
	}

	/**
	 * 获取data/task目录的File对象
	 */
	public static File getTaskDir(){
		return FileHelper.getDir(getDataDir(),"task");
	}
	
	/**
	 * 获取任务配置文件
	 */
	public static File getTaskConfigFile(File taskDir){
		return new File(taskDir,"task.conf");
	}

	/**
	 * 获取config文件夹
	 */
	public static File getConfigDir(){
		return FileHelper.getDir("config");
	}
	/**
	 * 获取配置文件
	 */
	public static File getConfigFile(){
		return FileHelper.getFile(getConfigDir(),"app.conf",true);
	}

	/**
	 * 获取client文件夹
	 */
	public static File getClientDir(){
		return FileHelper.getDir(getDataDir(),"client");
	}
	/**
	 * 获取client夹下的文件
	 */
	public static File getClientFile(String ip,int port) {
		return getClientFile(ip,String.valueOf(port),false);
	}
	/**
	 * 获取client夹下的文件
	 */
	public static File getClientFile(String ip,String port, boolean createNew) {
		String fileName = ip + "-" + port;
		return FileHelper.getFile(getClientDir(), fileName, createNew);
	}
}
