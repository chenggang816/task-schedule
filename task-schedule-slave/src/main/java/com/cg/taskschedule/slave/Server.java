package com.cg.taskschedule.slave;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.cg.common.util.FileHelper;
import com.cg.taskschedule.slave.msg.MsgCreator;
import com.cg.taskschedule.slave.msg.MsgHandler;
import com.cg.taskschedule.slave.msg.MsgHandlerFactory;

public class Server extends Object{
	public static final int port = getPortFromConfig();  
	//����һ��ServerSocket�����ڶ˿�port��  
	static ServerSocket server = null;
	//server���Խ�������Socket����������server��accept����������ʽ��  
	static Socket socket = null;
	BufferedReader in = null;
	PrintWriter out = null;

	public Server() {
		System.err.println("�����ѿ����������˿�Ϊ��" + port);
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("\n\t�쳣���޷��ڶ˿�" + port + "������SocketServer,Server Console���˳�");
			System.exit(0);
		}  
	}

	public static void main(String args[]){
		new Server().start();
		System.out.println("Server has exited.");
	}

	private void start() {
		try {
			while(true){
				//����������ֱ�����յ��ͻ�����Ϣ���Ż����ִ��
				socket = server.accept();

				//��socket����
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				StringBuilder msg = new StringBuilder();
				String line = null;
				while((line = in.readLine()) != null){
					System.out.println("From Manager:" + line);
					msg.append(line);
					msg.append("\n");
				}
				msg.deleteCharAt(msg.length() - 1);
				MsgHandler handler = MsgHandlerFactory.getMsgHandler(msg.toString());
				String strReply = handler.handle();
				if(strReply != null){
					send(strReply);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	} 

	public static void send(String strMsg){
		if(socket == null) return;
		PrintWriter out = null;
		try {
			//��socket���
			out = new PrintWriter(socket.getOutputStream(),true);
			out.println(strMsg);
			socket.shutdownOutput();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			out.close();
		}
		
	}
	
	private static int getPortFromConfig(){
		int port = 30000;
		File conf = FileMgr.getConfigFile();
		if(conf == null) return port;
		String text = FileHelper.ReadAllFromFile(conf);
		try{
			return Integer.parseInt(text);
		}catch(NumberFormatException e){
			return port;
		}
	}
	
	/**
	 * ��ȡServerSocket����
	 */
	public static void receiveFile(File file){
		if(file == null) throw new RuntimeException("�ļ�����Ϊ�գ�");
		File parent = file.getParentFile();
		if(!parent.exists()) parent.mkdirs();
		DataInputStream dis=null;  
		DataOutputStream dos = null;
		Socket socket = null;
		try {
			socket = server.accept();
			dis = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));
			dos = new DataOutputStream(   
		            new BufferedOutputStream(new BufferedOutputStream(
		                new FileOutputStream(file))));
			long len = dis.readLong();
			System.out.println("�ļ��ĳ���Ϊ:" + len/1024/1024 + "    MB");
			System.out.println("��ʼ�����ļ�!");
			byte[] buffer = new byte[1024];
			long passedlen = 0;
			int read;
			while((read = dis.read(buffer)) != -1){
				passedlen += read;   
//	            System.out.println("�ļ�������" + (passedlen * 100 / len) + "%");   
	            dos.write(buffer, 0, read);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}finally{
			try {
				dis.close();
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
