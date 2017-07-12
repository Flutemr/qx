package com.ibeifeng.offline.data.analysis.platform.stats;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.hadoop.conf.Configuration;

import com.ibeifeng.offline.data.analysis.platform.dimension.BaseDimension;
import com.ibeifeng.offline.data.analysis.platform.dimension.operation.IDimensionOperator;


/**
 * 自定义的配合自定义output进行具体sql输出的类
 * 
 * @author ibeifeng
 *
 */
public interface IOutputCollector {

    /**
     * 具体执行统计数据插入的方法
     * 
     * @param conf
     * @param key
     * @param value
     * @param pstmt
     * @param converter
     * @throws SQLException
     * @throws IOException
     */
    public void collect(Configuration conf, BaseDimension key, BaseStatsValueWritable value, PreparedStatement pstmt, IDimensionOperator converter) throws SQLException, IOException;
}
