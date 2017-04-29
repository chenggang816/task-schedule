package com.cg.taskschedule.master.net;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cg.taskschedule.master.IpPortPair;
import com.cg.taskschedule.master.msg.MsgCreator;
import com.cg.taskschedule.master.msg.MsgHandler;
import com.cg.taskschedule.master.msg.MsgHandlerFactory;
import com.cg.taskschedule.master.msg.ReplyMsgHandler;
import com.cg.taskschedule.master.msg.UnresolvedMsgHandler;

public class SocketHelper {
	
	class Monitor{
		public Monitor(int count){
			this.count = count;
		}
		private int count;
		synchronized public void reduceCount(int count){
			this.count -= count;
		}
		synchronized public int getCount(){
			System.out.println(this.count);
			return this.count;
		}
	}
	/*
	 * 测试ips里的每个ip对应port端口的服务是否开启
	 * 返回的Map的key为ip,value不为null表示成功开启
	 */
	public static Map<IpPortPair, Client> tryCommunicate(List<IpPortPair> ippList){
		final Map<IpPortPair,Client> map = new HashMap<IpPortPair,Client>();
		final Monitor monitor = new SocketHelper().new Monitor(ippList.size());
		final Set<IpPortPair> ipSet = new HashSet<IpPortPair>(ippList);
		for(final IpPortPair ipp:ippList){
			new Thread(){
				public void run(){
					Client client = null;
					try {
						client = new Client(ipp.getIp(),ipp.getPort());
						this.setName(ipp.getIp());
					} catch (IOException e) {
						e.printStackTrace();
					}
					String strReply = client.send(MsgCreator.createHelloMsg(ipp.getIp()));
					if(strReply == null){
						map.put(ipp, null);
					}else{
						MsgHandler handler = MsgHandlerFactory.getMsgHandler(strReply);
						if(handler instanceof ReplyMsgHandler){
							map.put(ipp, client);
						}else{
							map.put(ipp, null);
						}
						
						System.out.println(strReply);
					}
					monitor.reduceCount(1);
					ipSet.remove(ipp);
					System.out.print(this.getName() + ":");
					if(monitor.getCount() <= 0){
						synchronized(monitor){
							monitor.notify();
						}
					}
				}
			}.start();
		}
		try {
			synchronized(monitor){
				monitor.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(IpPortPair ipp:ipSet){
			System.out.println("没有退出的线程:" + ipp);
		}
		return map;
	}
}
