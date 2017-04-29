package com.cg.taskschedule.master.tools;

import java.io.File;

import com.cg.common.util.FileHelper;

public class FileMgr {
	/**
	 * ��ȡdata�ļ��е�File����
	 */
	public static File getDataDir(){
		return FileHelper.getDir("data");
	}

	/**
	 * ��ȡtasks.json��File����
	 */
	public static File getTaskAllJsonFile(){
		return FileHelper.getFile(getDataDir(),"TaskAll.json",true);
	}

	/**
	 * ��ȡdata/taskĿ¼��File����
	 */
	public static File getTaskDir(){
		return FileHelper.getDir(getDataDir(),"task");
	}
	
	/**
	 * ��ȡ���������ļ�
	 */
	public static File getTaskConfigFile(File taskDir){
		return new File(taskDir,"task.conf");
	}

	/**
	 * ��ȡconfig�ļ���
	 */
	public static File getConfigDir(){
		return FileHelper.getDir("config");
	}
	/**
	 * ��ȡ�����ļ�
	 */
	public static File getConfigFile(){
		return FileHelper.getFile(getConfigDir(),"app.conf",true);
	}

	/**
	 * ��ȡclient�ļ���
	 */
	public static File getClientDir(){
		return FileHelper.getDir(getDataDir(),"client");
	}
	/**
	 * ��ȡclient���µ��ļ�
	 */
	public static File getClientFile(String ip,int port) {
		return getClientFile(ip,String.valueOf(port),false);
	}
	/**
	 * ��ȡclient���µ��ļ�
	 */
	public static File getClientFile(String ip,String port, boolean createNew) {
		String fileName = ip + "-" + port;
		return FileHelper.getFile(getClientDir(), fileName, createNew);
	}
}
