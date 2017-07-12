package com.ibeifeng.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
/**
 * UDF案例
 * @author ibeifeng
 *
 */
public class Add extends UDF{
	
	public Integer evaluate(Integer a,Integer b){
		if(a==null || b==null) return null;	
		return a+b;
	}

	public Double evaluate(Double a,Double b){
		if(null == a || null ==b){
			return null;
		}
		return a+b;
	}
	
	public Integer evaluate(Integer... a){
		int total = 0;
		for(int i=0;i<a.length;i++){
			if(a[i] != null){
				total += a[i];
			}
		}
		return total;
	}
}
