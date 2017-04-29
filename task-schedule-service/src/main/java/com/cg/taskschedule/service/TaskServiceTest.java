package com.cg.taskschedule.service;

import com.cg.common.util.FileHelper;

import junit.framework.Assert;

/**
 * Hello world!
 *
 */
public class TaskServiceTest 
{
    public static void main( String[] args )
    {
    	Task task = new Task();
		task.setTaskDir("data/task/task1");
		task.setPath("task.jar");
		task.execute();
    }
}
