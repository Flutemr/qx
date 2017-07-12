package com.ibeifeng.offline.data.analysis.platform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import com.ibeifeng.offline.data.analysis.platform.util.IPSeekerExt;
import com.ibeifeng.offline.data.analysis.platform.util.IPSeekerExt.RegionInfo;

public class TestIPSeeker {
	
	public static void main(String[] args) throws FileNotFoundException {
		IPSeekerExt ipSeeker = IPSeekerExt.getInstance();
		
		List<String> ips = ipSeeker.getAllIp();
		
		System.out.println(ips.size());
		
		File ipDB = new File("ipDB.properties");
		
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(ipDB)));
		int i = 0;
		for(String ip:ips){
			RegionInfo info = ipSeeker.analyseIp(ip);
			ip = ip.replace(".", "_");
			pw.println(ip+"="+info.getCountry()+","+info.getProvince()+","+info.getCity());
			i ++ ;
			if(i % 500 == 0 ){
				pw.flush();
			}
		}
		
		pw.flush();
		pw.close();
	}

}
