package com.ibeifeng.offline.data.analysis.platform.dimension.operation;

import java.io.IOException;

import com.ibeifeng.offline.data.analysis.platform.dimension.BaseDimension;

/**
 * 定义Dimension操作接口
 * @author ibeifeng
 *
 */
public interface IDimensionOperator {
	/**
	 * 
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public int getDimensionIDByValue(BaseDimension value) throws IOException;

}
