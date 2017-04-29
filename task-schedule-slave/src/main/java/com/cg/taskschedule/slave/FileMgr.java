package com.cg.taskschedule.slave;

import java.io.File;

import com.cg.common.util.FileHelper;

public class FileMgr {
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
		return FileHelper.getFile(getConfigDir(),"server.conf",false);
	}
	
	/**
	 * 获取data文件夹的File对象
	 */
	public static File getDataDir(){
		return FileHelper.getDir("data");
	}
	
	/**
	 * 获取data/task目录的File对象
	 */
	public static File getTaskDir(){
		return FileHelper.getDir(getDataDir(),"task");
	}
	
	public static File getTheTaskDir(String taskName){
		return FileHelper.getDir(getTaskDir(),taskName,false);
	}
	
	public static File getTheTaskConfigFile(String taskName){
		File dir = getTheTaskDir(taskName);
		if(dir == null || dir.exists() == false){
			return null;
		}
		return FileHelper.getFile(dir, "task.conf", false);
	}
}
