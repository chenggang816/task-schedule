package com.cg.taskschedule.service;

import java.io.File;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TaskTest extends TestCase {

	public void testExecute() {
		Task task = new Task();
		task.setTaskDir(System.getProperty("user.dir") + "/data/task/task1/");
		task.setPath("task.jar");
		Assert.assertTrue(task.execute());
	}

}
