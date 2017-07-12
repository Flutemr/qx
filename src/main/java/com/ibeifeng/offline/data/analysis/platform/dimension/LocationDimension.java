package com.ibeifeng.offline.data.analysis.platform.dimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ibeifeng.offline.data.analysis.platform.common.GlobalConstants;
/**
 * 地域维度
 * @author ibeifeng
 *
 */
public class LocationDimension extends BaseDimension{
	
	private int id;
	private String country;
	private String province;
	private String city;
	
	public LocationDimension() {
		super();
	}

	public LocationDimension(String country, String province, String city) {
		super();
		this.country = country;
		this.province = province;
		this.city = city;
	}
	
	
	
	public LocationDimension(int id, String country, String province, String city) {
		this(country,province,city);
		this.id = id;
	}

	/**
	 * 构造多个地域维度的对象集合
	 * @param country
	 * @param province
	 * @param city
	 * @return
	 */
	public static List<LocationDimension> buildList(String country, String province, String city){
		if(StringUtils.isBlank(country)){
			country = GlobalConstants.DEFAULT_VALUE;
			province = GlobalConstants.DEFAULT_VALUE;
			city = GlobalConstants.DEFAULT_VALUE;
		}
		
		if(StringUtils.isBlank(province)){
			province = GlobalConstants.DEFAULT_VALUE;
			city = GlobalConstants.DEFAULT_VALUE;
		}
		
		if(StringUtils.isBlank(city)){
			city = GlobalConstants.DEFAULT_VALUE;
		}
		
		List<LocationDimension> lds = new ArrayList<LocationDimension>();
		lds.add(new LocationDimension(country,province,city));
		lds.add(new LocationDimension(country,GlobalConstants.VALUE_OF_ALL,GlobalConstants.VALUE_OF_ALL));
		lds.add(new LocationDimension(country,province,GlobalConstants.VALUE_OF_ALL));
		lds.add(new LocationDimension(GlobalConstants.VALUE_OF_ALL,GlobalConstants.VALUE_OF_ALL,GlobalConstants.VALUE_OF_ALL));
		return lds;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(id);
		out.writeUTF(country);
		out.writeUTF(province);
		out.writeUTF(city);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.id = in.readInt();
		this.country = in.readUTF();
		this.province = in.readUTF();
		this.city = in.readUTF();
	}

	@Override
	public int compareTo(BaseDimension o) {
		
		if(o == this) return 0;
		
		LocationDimension ld = (LocationDimension) o;
		int result = Integer.compare(this.id, ld.getId());
		if(result != 0) return result;
		
		result = this.country.compareTo(ld.getCountry());
		if(result != 0) return result;
		
		result = this.province.compareTo(ld.getProvince());
		if(result != 0) return result;
		
		result = this.city.compareTo(ld.getCity());
		return result;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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
}
