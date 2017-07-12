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
 * 添加了浏览器维度的活跃会员分析Collector
 * @author ibeifeng
 *
 */
public class ActiveMemberBrowserCollector implements IOutputCollector{

	@Override
	public void collect(Configuration conf, BaseDimension key, BaseStatsValueWritable value, PreparedStatement pstmt,
			IDimensionOperator converter) throws SQLException, IOException {
		// 进行强制后获取对应的值
        StatsUserDimension statsMember = (StatsUserDimension) key;
        IntWritable activeMemberValue = (IntWritable) ((MapWritableValue) value).getValue().get(new IntWritable(-1));

        // 进行参数设置
        int i = 0;
        pstmt.setInt(++i, converter.getDimensionIDByValue(statsMember.getStatsCommon().getPlatform()));
        pstmt.setInt(++i, converter.getDimensionIDByValue(statsMember.getStatsCommon().getDate()));
        pstmt.setInt(++i, converter.getDimensionIDByValue(statsMember.getBrowser()));
        pstmt.setInt(++i, activeMemberValue.get());
        pstmt.setString(++i, conf.get(GlobalConstants.RUNNING_DATE_PARAMES));
        pstmt.setInt(++i, activeMemberValue.get());

        // 添加到batch中
        pstmt.addBatch();
		
	}

}
