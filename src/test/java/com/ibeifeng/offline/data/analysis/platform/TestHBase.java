package com.ibeifeng.offline.data.analysis.platform;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.MultipleColumnPrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;

import com.ibeifeng.offline.data.analysis.platform.common.EventLogConstants;


public class TestHBase {
	
	public static void main(String[] args) throws MasterNotRunningException, ZooKeeperConnectionException, IOException {
		
		TestHBase testHBase = new TestHBase();
		Configuration conf = new Configuration();
		conf.set("fs.defaultFS", "hdfs://project.ibeifeng.com:8020");
     	conf.set("yarn.resourcemanager.hostname", "project.ibeifeng.com");
     	conf.set("hbase.zookeeper.quorum", "project.ibeifeng.com");
		conf = HBaseConfiguration.create(conf);
		
		
		Scan scan = new Scan();
        // 定义hbase扫描的开始rowkey和结束rowkey
        //scan.setStartRow(Bytes.toBytes("" + startDate));
        //scan.setStopRow(Bytes.toBytes("" + endDate));

        FilterList filterList = new FilterList();
        // 定义mapper中需要获取的列名
        String[] columns = new String[] { EventLogConstants.LOG_COLUMN_NAME_UUID, // 用户id
                EventLogConstants.LOG_COLUMN_NAME_COUNTRY,
                EventLogConstants.LOG_COLUMN_NAME_PROVINCE,
                EventLogConstants.LOG_COLUMN_NAME_CITY
        };
        filterList.addFilter(testHBase.getColumnFilter(columns));

        scan.setAttribute(Scan.SCAN_ATTRIBUTES_TABLE_NAME, Bytes.toBytes(EventLogConstants.HBASE_NAME_EVENT_LOGS));
        scan.setFilter(filterList);
        
        HTable table=new HTable(conf,"event_logs"); 
        ResultScanner result = table.getScanner(scan);
        Result rr = null;
        while( (rr = result.next()) != null){
        	
        	List<Cell> cells = rr.listCells();
        	
        	for(Cell c:cells){
        		//System.out.println("Row:" + Bytes.toString(c.getRow()));
        		//System.out.println("Family:" + Bytes.toString(c.getFamily()));
        		//System.out.println("Qualifier:" + Bytes.toString(c.getQualifier()));
        		//System.out.println("Value:" + Bytes.toString(c.getValue()));
        		
        		System.out.println("Row:" + Bytes.toString(c.getRow()));
        		System.out.println("Family:Qualifier -->" + Bytes.toString(c.getFamily()) +":" + Bytes.toString(c.getQualifier()));
        		System.out.println("Value:" + Bytes.toString(c.getValue()));
        	}
        	
        }
        
        table.close();
	}
	
	   /**
     * 获取这个列名过滤的column
     * 
     * @param columns
     * @return
     */
    private Filter getColumnFilter(String[] columns) {
        int length = columns.length;
        byte[][] filter = new byte[length][];
        for (int i = 0; i < length; i++) {
            filter[i] = Bytes.toBytes(columns[i]);
        }
        return new MultipleColumnPrefixFilter(filter);
    }

}
