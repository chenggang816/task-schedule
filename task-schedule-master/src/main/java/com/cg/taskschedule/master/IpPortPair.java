package com.cg.taskschedule.master;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IpPortPair implements Comparable<IpPortPair>{
	private String ip;
	private int port;
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	public IpPortPair(String ip,int port) {
		this.ip = ip;
		this.port = port;
	}
	public IpPortPair(String ip){
		this.ip = ip;
		this.port = 30000;
	}
	public static List<IpPortPair> toIpPortList(List<String> ipList){
		IpPortPair[] ippArray = new IpPortPair[ipList.size()];
		for(int i = 0; i < ipList.size(); i++){
			String ip = ipList.get(i);
			ippArray[i] = new IpPortPair(ip);
		}
		return Arrays.asList(ippArray);
	}
	public static List<String> toIpList(List<IpPortPair> ippList){
		List<String> ipList = new ArrayList<String>();
		for(IpPortPair ipp:ippList){
			ipList.add(ipp.ip);
		}
		return ipList;
	}
	
	@Override
	public int compareTo(IpPortPair o) {
		int r = ip.compareTo(o.ip);
		if(r == 0)
			r = port - o.port;
		return r;
	}
	@Override
	public String toString(){
		return ip + ":" + port;
	}
}