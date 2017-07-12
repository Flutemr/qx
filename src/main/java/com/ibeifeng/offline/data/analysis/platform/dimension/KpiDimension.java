package com.ibeifeng.offline.data.analysis.platform.dimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 
 * @author ibeifeng
 *
 */
public class KpiDimension extends BaseDimension{
	
	private int id;
	private String kpiName;
	
	public KpiDimension() {
		super();
	}

	public KpiDimension(int id, String kpiName) {
		super();
		this.id = id;
		this.kpiName = kpiName;
	}
	
	public KpiDimension(String kpiName) {
		super();
		this.kpiName = kpiName;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(id);
		out.writeUTF(kpiName);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.id = in.readInt();
		this.kpiName = in.readUTF();
	}

	@Override
	public int compareTo(BaseDimension o) {
		if(o == this) return 0;
		
		KpiDimension kd = (KpiDimension) o;
		
		int result = Integer.compare(this.id, kd.getId());
		if(result == 0){
			return this.kpiName.compareTo(kd.getKpiName());
		}
		return result;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKpiName() {
		return kpiName;
	}

	public void setKpiName(String kpiName) {
		this.kpiName = kpiName;
	}
}
