package com.ibeifeng.offline.data.analysis.platform.stats.mem;

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
 * 活跃会员分析Reducer
 * @author ibeifeng
 *
 */
public class ActiveMemberReducer 
	extends Reducer<StatsUserDimension, TimeOutputValue, StatsUserDimension, MapWritableValue>{
	
	private Set<String> unique = new HashSet<String>();
	
	private MapWritableValue outputValue = new MapWritableValue();
	
	private MapWritable map = new MapWritable();

	@Override
	protected void reduce(StatsUserDimension statsUserDimension, Iterable<TimeOutputValue> arg1,
			Reducer<StatsUserDimension, TimeOutputValue, StatsUserDimension, MapWritableValue>.Context context)
			throws IOException, InterruptedException {
		
		try {
			// 将memberid添加到set里面去重
			for(TimeOutputValue tov : arg1){
				unique.add(tov.getId());
			}
			// 设置输出
			this.outputValue.setKpi(KpiType.valueOf(statsUserDimension.getStatsCommon().getKpi().getKpiName()));
			this.map.put(new IntWritable(-1), new IntWritable(unique.size()));
			
			this.outputValue.setValue(map);
			
			context.write(statsUserDimension, outputValue);
		} finally{
			// 注意一定要记得清空
			this.unique.clear();
		}
	}
}
