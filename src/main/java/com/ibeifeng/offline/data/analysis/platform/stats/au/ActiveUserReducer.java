package com.ibeifeng.offline.data.analysis.platform.stats.au;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.mapreduce.Reducer;

import com.ibeifeng.offline.data.analysis.platform.common.KpiType;
import com.ibeifeng.offline.data.analysis.platform.dimension.stats.StatsUserDimension;
import com.ibeifeng.offline.data.analysis.platform.stats.MapWritableValue;
import com.ibeifeng.offline.data.analysis.platform.stats.TimeOutputValue;


/**
 * 统计active user， 其实就是计算本一组中这个uuid的个数
 * 
 * @author ibeifeng
 *
 */
public class ActiveUserReducer extends Reducer<StatsUserDimension, TimeOutputValue, StatsUserDimension, MapWritableValue> {
    private Set<String> unique = new HashSet<String>();
    private MapWritableValue outputValue = new MapWritableValue();
    private MapWritable map = new MapWritable();

    @Override
    protected void reduce(StatsUserDimension key, Iterable<TimeOutputValue> values, Context context) throws IOException, InterruptedException {
        try {
            // 将uuid添加到set集合中去，方便进行统计uuid的去重个数
            for (TimeOutputValue value : values) {
                this.unique.add(value.getId());
            }

            // 设置kpi
            this.outputValue.setKpi(KpiType.valueOfName(key.getStatsCommon().getKpi().getKpiName()));
            // 设置value
            this.map.put(new IntWritable(-1), new IntWritable(this.unique.size()));
            this.outputValue.setValue(this.map);

            // 进行输出
            context.write(key, this.outputValue);
        } finally {
            // 清空操作
            this.unique.clear();
        }

    }
}
