package com.ibeifeng.offline.data.analysis.platform;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.ibeifeng.offline.data.analysis.platform.util.AddressBean;
import com.ibeifeng.offline.data.analysis.platform.util.AddressUtils;

/**
 * 
 * @author ibeifeng
 *
 */
public class TestAddressUtils {
	
	@Test
	public void testGetAddresses() throws UnsupportedEncodingException{
		AddressUtils addressUtils = new AddressUtils();
		AddressBean  adbean = addressUtils.getAddresses("ip=61.135.169.125", "utf-8");
		System.out.println(adbean);
	}

}
