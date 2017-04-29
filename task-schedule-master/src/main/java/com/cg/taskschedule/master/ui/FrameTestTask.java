package com.cg.taskschedule.master.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.cg.taskschedule.master.WorkFlowTestTask;

public class FrameTestTask extends JFrame {
	
	JPanel jp1 = new JPanel();
	JPanel jp2 = new JPanel();
	JPanel jp3 = new JPanel();
	JLabel lb = new JLabel("选择主机与任务，点击执行任务按钮进行测试");
	JComboBox cb1,cb2;
	JButton btnTest = new JButton("执行任务");
	JTextArea textArea = new JTextArea();
	WorkFlowTestTask workFlow = new WorkFlowTestTask();
	
	public FrameTestTask() {
		jp1.setLayout(new FlowLayout());
		jp1.add(lb);
		
		cb1 = new JComboBox(workFlow.getComboboxModel("HOST"));
		cb2 = new JComboBox(workFlow.getComboboxModel("TASK"));
		jp2.setLayout(new GridLayout(1,2));
		jp2.add(jp3);
		jp2.add(new JScrollPane(textArea));
		
		jp3.setLayout(new GridLayout(6,1,5,5));
		jp3.add(new JLabel("主机"));
		jp3.add(cb1);
		jp3.add(new JLabel("任务"));
		jp3.add(cb2);
		jp3.add(new JLabel());
		jp3.add(btnTest);
		
		btnTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				workFlow.doExecuteTask();
			}
		});
		
		
		add(jp1,BorderLayout.NORTH);
		add(jp2,BorderLayout.CENTER);
		
		setTitle("任务测试");
		setSize(600,300);
		setLocation(400,300);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
