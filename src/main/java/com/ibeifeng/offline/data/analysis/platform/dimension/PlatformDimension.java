package com.ibeifeng.offline.data.analysis.platform.dimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ibeifeng.offline.data.analysis.platform.common.GlobalConstants;
/**
 * 平台维度（平台名称）
 * @author ad
 *
 */
public class PlatformDimension extends BaseDimension{
	
	private int id;
	private String platformName;
	public PlatformDimension(){
		super();
	}
	public PlatformDimension(String platformName) {
		super();
		this.platformName = platformName;
	}
	public PlatformDimension(int id, String platformName) {
		super();
		this.id = id;
		this.platformName = platformName;
	}
	/**
	 * 构造多个平台维度的对象集合
	 * @param platformName
	 * @return
	 */
	public static List<PlatformDimension> buildList(String platformName){
		
		if(StringUtils.isBlank(platformName)){
			platformName = GlobalConstants.DEFAULT_VALUE;
		}
		List<PlatformDimension> pds = new ArrayList<PlatformDimension>();
		pds.add(new PlatformDimension(platformName));
		pds.add(new PlatformDimension(GlobalConstants.VALUE_OF_ALL));
		return pds;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(id);
		out.writeUTF(platformName);
	}
	@Override
	public void readFields(DataInput in) throws IOException {
		this.id = in.readInt();
		this.platformName = in.readUTF();
	}
	@Override
	public int compareTo(BaseDimension o) {
		if(this == o) return 0;
		PlatformDimension pfd = (PlatformDimension) o;
		int result = Integer.compare(id, pfd.getId());
		if(0 == result){
			return this.platformName.compareTo(pfd.getPlatformName());
		}
		return result;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPlatformName() {
		return platformName;
	}
	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((platformName == null) ? 0 : platformName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlatformDimension other = (PlatformDimension) obj;
		if (id != other.id)
			return false;
		if (platformName == null) {
			if (other.platformName != null)
				return false;
		} else if (!platformName.equals(other.platformName))
			return false;
		return true;
	}
}
