package com.ibeifeng.offline.data.analysis.platform.stats.mem;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;

import com.ibeifeng.offline.data.analysis.platform.common.GlobalConstants;
import com.ibeifeng.offline.data.analysis.platform.dimension.BaseDimension;
import com.ibeifeng.offline.data.analysis.platform.dimension.operation.IDimensionOperator;
import com.ibeifeng.offline.data.analysis.platform.dimension.stats.StatsUserDimension;
import com.ibeifeng.offline.data.analysis.platform.stats.BaseStatsValueWritable;
import com.ibeifeng.offline.data.analysis.platform.stats.IOutputCollector;
import com.ibeifeng.offline.data.analysis.platform.stats.MapWritableValue;
/**
 * 活跃会员分析Collector
 * @author ibeifeng
 *
 */
public class ActiveMemberCollector implements IOutputCollector{

	@Override
	public void collect(Configuration conf, BaseDimension key, BaseStatsValueWritable value, PreparedStatement pstmt,
			IDimensionOperator converter) throws SQLException, IOException {
		
		StatsUserDimension sud = (StatsUserDimension) key;
		
		MapWritableValue mwv = (MapWritableValue) value;
		
		IntWritable activeMember = (IntWritable) mwv.getValue().get(new IntWritable(-1));
		
		int i = 0;
		pstmt.setInt(++i, converter.getDimensionIDByValue(sud.getStatsCommon().getPlatform()));
        pstmt.setInt(++i, converter.getDimensionIDByValue(sud.getStatsCommon().getDate()));
        pstmt.setInt(++i, activeMember.get());
        pstmt.setString(++i, conf.get(GlobalConstants.RUNNING_DATE_PARAMES));
        pstmt.setInt(++i, activeMember.get());

        // 添加到batch中
        pstmt.addBatch();
		
	}

}
