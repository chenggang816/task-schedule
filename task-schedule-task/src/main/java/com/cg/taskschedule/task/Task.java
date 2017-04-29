package com.cg.taskschedule.task;

import java.awt.FlowLayout;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.cg.common.util.*;

public class Task extends JFrame{
	JTextArea ta = new JTextArea();
	static int total = 5;
	public Task() {
		add(new JScrollPane(ta));
		setTitle("»ŒŒÒ≤‚ ‘");
		setSize(500,400);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void work(){
		System.out.println("task started ...");
		StringBuilder text = new StringBuilder("task started ...\n");
		ta.setText(text.toString());
		
		try {
			File config = new File(System.getProperty("user.dir") + "/config");
			if(config.exists()){
				total = Integer.parseInt(FileHelper.ReadAllFromFile(config));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try{
			text.append(String.format("\ttotal\t\t%d\n", total));
			ta.setText(text.toString());
			for(int i = 0; i < total; i++){
				Thread.sleep(1000);
				double finished = (i + 1) * 100.0/ total;
				text.append(String.format("\n\tfinished\t%.2f%%\n", finished));
				ta.setText(text.toString());
			}
			text.append("task finished ...");
			ta.setText(text.toString());
			Thread.sleep(500);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		dispose();
	}

	public static void main(String[] args) {
		if(args.length > 0){
			total = Integer.parseInt(args[0]);
		}
		new Task().work();
	}

}
