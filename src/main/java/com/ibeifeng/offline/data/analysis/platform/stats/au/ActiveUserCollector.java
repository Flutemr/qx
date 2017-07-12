package com.ibeifeng.offline.data.analysis.platform.stats.au;

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
 * 
 * @author ibeifeng
 *
 */
public class ActiveUserCollector implements IOutputCollector {

    @Override
    public void collect(Configuration conf, BaseDimension key, BaseStatsValueWritable value, PreparedStatement pstmt, IDimensionOperator converter) throws SQLException, IOException {
        // 进行强制后获取对应的值
        StatsUserDimension statsUser = (StatsUserDimension) key;
        IntWritable activeUserValue = (IntWritable) ((MapWritableValue) value).getValue().get(new IntWritable(-1));

        // 进行参数设置
        int i = 0;
        pstmt.setInt(++i, converter.getDimensionIDByValue(statsUser.getStatsCommon().getPlatform()));
        pstmt.setInt(++i, converter.getDimensionIDByValue(statsUser.getStatsCommon().getDate()));
        pstmt.setInt(++i, activeUserValue.get());
        pstmt.setString(++i, conf.get(GlobalConstants.RUNNING_DATE_PARAMES));
        pstmt.setInt(++i, activeUserValue.get());

        // 添加到batch中
        pstmt.addBatch();
    }
}
