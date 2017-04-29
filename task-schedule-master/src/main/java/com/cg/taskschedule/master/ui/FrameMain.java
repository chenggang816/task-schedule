package com.cg.taskschedule.master.ui;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.cg.common.util.NetHelper;
import com.cg.taskschedule.master.WorkFlowMain;
import com.cg.taskschedule.master.net.SocketHelper;

public class FrameMain extends JFrame{
	JMenuBar menuBar;
	JMenu menuFile,menuTask;
	JMenuItem menuItemLoadConfigFile,menuItemEditConfigFile,menuItemViewConfigFile;
	JMenuItem menuItemTestTask;
	JButton btnGainAllIp,btnGainHostNames;
	JButton btnTestService,btnCheckTaskUpdate,btnTaskUpdate;
	JPanel panelTop;
	JTable table;
	
	WorkFlowMain workFlow = WorkFlowMain.getWorkFlow();

	public FrameMain() {
		initialize();
	}
	
	
	
 	private void initialize() {
		panelTop = new JPanel(new GridLayout(9, 1, 35, 5));
		panelTop.setSize(200, 400);
		
		btnGainAllIp = new JButton("��ȡ����������IP");
		btnGainAllIp.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				workFlow.doGainAllIp();
			}
		});
		panelTop.add(btnGainAllIp);
		
		btnGainHostNames = new JButton("��ȡ������");
		panelTop.add(btnGainHostNames);
		btnGainHostNames.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!workFlow.canWork()){
					return;
				}
				new Thread(){
					public void run(){
						try {
							btnGainHostNames.setText("���ڻ�ȡ������");
							btnGainHostNames.setEnabled(false);
							workFlow.doGainHostNames();
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}finally{
							btnGainHostNames.setText("��ȡ������");
							btnGainHostNames.setEnabled(true);
						}
						
					}
				}.start();
			}
		});
		
		btnTestService = new JButton("���Է����Ƿ���");
		panelTop.add(btnTestService);
		btnTestService.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!workFlow.canWork()){
					return;
				}
				new Thread(){
					public void run(){
						String strBtnTxt = btnTestService.getText();
						btnTestService.setText("���ڲ��Է���");
						btnTestService.setEnabled(false);
						workFlow.doTestService();
						btnTestService.setText(strBtnTxt);
						btnTestService.setEnabled(true);
						System.out.println("����������");
					}
				}.start();
			}
		});
		
		btnCheckTaskUpdate = new JButton("����������");
		panelTop.add(btnCheckTaskUpdate);
		btnCheckTaskUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				workFlow.doCheckTaskUpdate();
			}
		});
		
		btnTaskUpdate = new JButton("�������");
		panelTop.add(btnTaskUpdate);
		btnTaskUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!workFlow.canWork()) return;
				workFlow.doTaskUpdate();
			}
		});
		
		menuBar = new JMenuBar();
		menuFile = new JMenu("�ļ�");
		menuTask = new JMenu("����");
		menuBar.add(menuFile);
		menuBar.add(menuTask);
		
		menuItemLoadConfigFile = new JMenuItem("�������ļ��м�������");
		menuFile.add(menuItemLoadConfigFile);
		menuItemLoadConfigFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				workFlow.doLoadHostsFromConfig();
			}
		});

		menuItemEditConfigFile = new JMenuItem("�༭�����ļ�...");
		menuFile.add(menuItemEditConfigFile);
		menuItemEditConfigFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				workFlow.doEditConfigFile();
			}
		});
		
		menuItemViewConfigFile = new JMenuItem("�鿴�����ļ�...");
		menuFile.add(menuItemViewConfigFile);
		menuItemViewConfigFile.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				workFlow.doViewConfigFile();
			}
		});
		
		
		menuItemTestTask = new JMenuItem("�������в���");
		menuTask.add(menuItemTestTask);
		menuItemTestTask.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new FrameTestTask();
			}
		});
		
		table = new JTable(workFlow.getTabelModel());
		table.setRowHeight(45);		
		//���䷽ʽ����  
        DefaultTableCellRenderer d = new DefaultTableCellRenderer();            
        //���ñ��Ԫ��Ķ��뷽ʽΪ���ж��뷽ʽ  
        d.setHorizontalAlignment(JLabel.CENTER);  
        for(int i = 0; i< table.getColumnCount();i++)  
        {  
            TableColumn col = table.getColumn(table.getColumnName(i));  
            col.setCellRenderer(d);  
        }  
		
		this.add(menuBar,BorderLayout.NORTH);
        this.add(panelTop,BorderLayout.EAST);
		this.add(new JScrollPane(table),BorderLayout.CENTER);
		
		this.setTitle("������ȹ���");
		this.setLocation(150,100);
		this.setSize(1000,600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		this.setVisible(true);
	}
}
