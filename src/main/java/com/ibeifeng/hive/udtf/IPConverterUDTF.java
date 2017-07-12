package com.ibeifeng.hive.udtf;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

import com.ibeifeng.offline.data.analysis.platform.util.IPSeekerExt;
import com.ibeifeng.offline.data.analysis.platform.util.IPSeekerExt.RegionInfo;

/**
 * 自定义实现解析IP的udtf
 * @author ibeifeng
 *
 */
public class IPConverterUDTF extends GenericUDTF{
	

	@Override
	public StructObjectInspector initialize(StructObjectInspector argOIs) throws UDFArgumentException {
		
		if(argOIs.getAllStructFieldRefs().size() != 1){
			throw new UDFArgumentException("必须有一个参数！");
		}
		
		ArrayList<String> fieldNames = new ArrayList<String>();
		ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();
		// 声明函数解析结果的字段名称
		fieldNames.add("country");
		fieldNames.add("province");
		fieldNames.add("city");
		// 声明函数 解析结果的字段数据类型
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
		
		return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);
	}

	private IPSeekerExt seeker = IPSeekerExt.getInstance();
	/**
	 * 真正实现函数的处理逻辑
	 */
	@Override
	public void process(Object[] args) throws HiveException {
		if(args != null && args.length == 1){
			String ip = args[0].toString();
			RegionInfo info = seeker.analyseIp(ip);
			
			if(info == null){
				// 使用默认的
				info = new RegionInfo();
			}
			
			List<String> result = new ArrayList<String>();
			result.add(info.getCountry());
			result.add(info.getProvince());
			result.add(info.getCity());
			
			super.forward(result.toArray(new String[0]));
		}
	}

	/**
	 * 对需要清理的对象进行处理
	 * @throws HiveException
	 */
	@Override
	public void close() throws HiveException {
		// TODO Auto-generated method stub
	}
}
