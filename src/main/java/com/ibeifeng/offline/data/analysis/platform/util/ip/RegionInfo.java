package com.ibeifeng.offline.data.analysis.platform.util.ip;

import com.ibeifeng.offline.data.analysis.platform.common.GlobalConstants;
/**
 * 地址信息
 * @author ibeifeng
 *
 */
public class RegionInfo {
	
	private String country = GlobalConstants.DEFAULT_VALUE;
    private String province = GlobalConstants.DEFAULT_VALUE;
    private String city = GlobalConstants.DEFAULT_VALUE;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "RegionInfo [country=" + country + ", province=" + province + ", city=" + city + "]";
    }

}
