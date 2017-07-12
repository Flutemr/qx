package com.ibeifeng.offline.data.analysis.platform.stats;

import org.apache.hadoop.io.Writable;

import com.ibeifeng.offline.data.analysis.platform.common.KpiType;


/**
 * 自定义顶级的输出value父类
 * 
 * @author ibeifeng
 *
 */
public abstract class BaseStatsValueWritable implements Writable {
    /**
     * 获取当前value对应的kpi值
     * 
     * @return
     */
    public abstract KpiType getKpi();
}
