package com.ibeifeng.offline.data.analysis.platform;

import org.junit.Test;

import com.ibeifeng.offline.data.analysis.platform.util.IPSeekerExt;
import com.ibeifeng.offline.data.analysis.platform.util.IPSeekerExt.RegionInfo;

public class TestIPSeekerExt {
	
	@Test
	public void testAnalyseIp(){
		IPSeekerExt ipSeeker = IPSeekerExt.getInstance();
		RegionInfo info = ipSeeker.analyseIp("115.239.211.112");
		System.out.println(info);
	}

}
