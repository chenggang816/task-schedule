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
	//定义一个ServerSocket监听在端口port上  
	static ServerSocket server = null;
	//server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的  
	static Socket socket = null;
	BufferedReader in = null;
	PrintWriter out = null;

	public Server() {
		System.err.println("服务已开启，开启端口为：" + port);
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("\n\t异常：无法在端口" + port + "上启动SocketServer,Server Console已退出");
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
				//阻塞方法，直到接收到客户端消息，才会继续执行
				socket = server.accept();

				//从socket读入
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
			//向socket输出
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
	 * 获取ServerSocket对象
	 */
	public static void receiveFile(File file){
		if(file == null) throw new RuntimeException("文件对象为空！");
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
			System.out.println("文件的长度为:" + len/1024/1024 + "    MB");
			System.out.println("开始接收文件!");
			byte[] buffer = new byte[1024];
			long passedlen = 0;
			int read;
			while((read = dis.read(buffer)) != -1){
				passedlen += read;   
//	            System.out.println("文件接收了" + (passedlen * 100 / len) + "%");   
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
