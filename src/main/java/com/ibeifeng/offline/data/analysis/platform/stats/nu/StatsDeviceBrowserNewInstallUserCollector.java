package com.ibeifeng.offline.data.analysis.platform.stats.nu;

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
 * 浏览器维度新增用户分析Collector工具类，即构造PreparedStatement
 * @author ibeifeng
 *
 */
public class StatsDeviceBrowserNewInstallUserCollector implements IOutputCollector {

    @Override
    /**
     * 给sql语句中的？赋值的方法
     */
    public void collect(Configuration conf, BaseDimension key, BaseStatsValueWritable value, PreparedStatement pstmt, IDimensionOperator converter) throws SQLException, IOException {
        StatsUserDimension statsUserDimension = (StatsUserDimension) key;
        MapWritableValue mapWritableValue = (MapWritableValue) value;
        IntWritable newInstallUsers = (IntWritable) mapWritableValue.getValue().get(new IntWritable(-1));

        int i = 0;
        pstmt.setInt(++i, converter.getDimensionIDByValue(statsUserDimension.getStatsCommon().getPlatform()));
        pstmt.setInt(++i, converter.getDimensionIDByValue(statsUserDimension.getStatsCommon().getDate()));
        pstmt.setInt(++i, converter.getDimensionIDByValue(statsUserDimension.getBrowser()));
        pstmt.setInt(++i, newInstallUsers.get());
        pstmt.setString(++i, conf.get(GlobalConstants.RUNNING_DATE_PARAMES));
        pstmt.setInt(++i, newInstallUsers.get());
        pstmt.addBatch();
    }

}
