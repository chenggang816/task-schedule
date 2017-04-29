package com.cg.taskschedule.master.net;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;

import com.cg.taskschedule.master.msg.MsgCreator;
import com.cg.taskschedule.master.tools.JSONMgr;

public class Client {
	Socket socket = null;
	String host = null;
	int port;
	public Client(String host,int port) throws UnknownHostException, IOException {
		this.host = host;
		this.port = port;

	}  
	public boolean connect(){
		//与服务端建立连接  
		try {
			socket = new Socket(host, port);
			return true;
		} catch (UnknownHostException e) {
			return false;
		} catch (IOException e) {
			return false;
		}  
	}
	public void close(){
		if(socket == null)
			return;
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String send(String strMsg,boolean waitForReply){
		if(!connect())
			return null;
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			out = new PrintWriter(socket.getOutputStream(),true);
			out.println(strMsg);
			socket.shutdownOutput();
			
			if(waitForReply){
				//写完以后进行读操作  
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				StringBuilder msg = new StringBuilder();
				String line ;
				while((line= in.readLine()) != null){
					System.out.println("From Worker:" + line);
					msg.append(line);
					msg.append("\n");
				}
				msg.deleteCharAt(msg.length() - 1);
				return msg.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("异常主机ip:" + host);
		}  finally{
			try {
				out.close();
				in.close(); 
				close();
			} catch (Exception e) {
				return null;
			} 
		}
		return null;
	}
	
	public String send(String strMsg){
		return send(strMsg, true);
	}
	public void sendFile(File file) {
		DataInputStream dis = null;
		DataOutputStream dos = null;
		connect();
		try {
			if(file == null) throw new IOException("文件为null");
			dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
			dos = new DataOutputStream(socket.getOutputStream());
			dos.writeLong(file.length());
			dos.flush();
			byte[] buffer = new byte[1024];
			int read;
			while((read = dis.read(buffer)) != -1){
				dos.write(buffer,0,read);
				dos.flush();
			}
			socket.shutdownOutput();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				dis.close();
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			close();
		}
	}
}
