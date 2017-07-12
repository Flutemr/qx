package com.ibeifeng.offline.data.analysis.platform.util;
/**
 * 使用淘宝ip库获取到的IP信息
 * @author ibeifeng
 *
 */
public class AddressBean {
	
	/**
	 * 国家
	 */
	private String country;
	
	/**
	 * 区域
	 */
	private String area;
	/**
	 * 省份
	 */
	private String region;
	/**
	 * 城市
	 */
	private String city;
	/**
	 * 地区
	 */
	private String county;
	/**
	 * ISP公司
	 */
	private String isp;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getIsp() {
		return isp;
	}

	public void setIsp(String isp) {
		this.isp = isp;
	}

	@Override
	public String toString() {
		return "AddressBean [country=" + country + ", area=" + area + ", region=" + region + ", city=" + city
				+ ", county=" + county + ", isp=" + isp + "]";
	}
}
