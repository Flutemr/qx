package com.ibeifeng.offline.data.analysis.platform.stats;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.WritableUtils;

import com.ibeifeng.offline.data.analysis.platform.common.KpiType;


public class MapWritableValue extends BaseStatsValueWritable {
	/**
	 * 
	 */
    private MapWritable value = new MapWritable();
    private KpiType kpi;

    public MapWritableValue() {
        super();
    }

    public MapWritableValue(MapWritable value, KpiType kpi) {
        super();
        this.value = value;
        this.kpi = kpi;
    }

    public MapWritable getValue() {
        return value;
    }

    public void setValue(MapWritable value) {
        this.value = value;
    }

    public void setKpi(KpiType kpi) {
        this.kpi = kpi;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        this.value.write(out);
        // 枚举类型序列化
        WritableUtils.writeEnum(out, this.kpi);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.value.readFields(in);
        // 枚举类型反序列化
        this.kpi = WritableUtils.readEnum(in, KpiType.class);
    }

    @Override
    public KpiType getKpi() {
        return this.kpi;
    }

}
