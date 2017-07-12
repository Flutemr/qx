package com.ibeifeng.offline.data.analysis.platform.stats.mem;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.MultipleColumnPrefixFilter;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.ibeifeng.offline.data.analysis.platform.common.EventLogConstants;
import com.ibeifeng.offline.data.analysis.platform.common.GlobalConstants;
import com.ibeifeng.offline.data.analysis.platform.dimension.stats.StatsUserDimension;
import com.ibeifeng.offline.data.analysis.platform.stats.MapWritableValue;
import com.ibeifeng.offline.data.analysis.platform.stats.TimeOutputValue;
import com.ibeifeng.offline.data.analysis.platform.stats.TransformerOutputFormat;
import com.ibeifeng.offline.data.analysis.platform.util.TimeUtil;

/**
 * 活跃会员分析Driver
 * @author ibeifeng
 *
 */
public class ActiveMemberRunner implements Tool{
	
	private static final Logger logger = LoggerFactory.getLogger(ActiveMemberRunner.class);
	
	private Configuration conf = null;
	
	public static void main(String[] args) {
		try {
			int exitcode = ToolRunner.run(new ActiveMemberRunner(), args);
			if(exitcode == 0){
				logger.info("任务运行成功！");
			}else{
				logger.info("任务运行失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setConf(Configuration conf) {
		// 添加客户端配置
		conf.set("fs.defaultFS", "hdfs://project.ibeifeng.com:8020/");
		conf.set("yarn.resourcemanager.hostname", "project.ibeifeng.com");
		conf.set("hbase.zookeeper.quorum", "project.ibeifeng.com:2181");
		conf.addResource("transformer-env.xml");
        conf.addResource("query-mapping.xml");
        conf.addResource("output-collector.xml");
        
       this.conf = HBaseConfiguration.create(conf);
	}

	@Override
	public Configuration getConf() {
		return this.conf;
	}

	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = this.getConf();
		
		this.processArgs(conf,args);
		
		Job job = Job.getInstance(conf);
		job.setJarByClass(ActiveMemberRunner.class);
		
		// 集群运行
//		TableMapReduceUtil.initTableMapperJob(this.initScans(job), 
//				ActiveMemberMapper.class, StatsUserDimension.class, TimeOutputValue.class, job);
		// 本地运行
		TableMapReduceUtil.initTableMapperJob(this.initScans(job), 
				ActiveMemberMapper.class, StatsUserDimension.class, TimeOutputValue.class, job,false);
		
		job.setReducerClass(ActiveMemberReducer.class);
		job.setOutputKeyClass(StatsUserDimension.class);
		job.setOutputValueClass(MapWritableValue.class);
		// 设置输出格式为自定义输出格式
		job.setOutputFormatClass(TransformerOutputFormat.class);
		return job.waitForCompletion(true) ? 0 : 1;
	}

	private void processArgs(Configuration conf,String[] args) {
		
		// 从参数中取出日期
		String date = null;
		for(int i = 0 ; i< args.length;i++){
			if("-d".equals(args[i])){
				if( i +1 < args.length){
					i ++;
					date = args[i];
					break;
				}
			}
		}
		
		if(StringUtils.isBlank(date) || !TimeUtil.isValidateRunningDate(date)){
			// 如果非法则默认昨天
			date = TimeUtil.getYesterday();
		}
		
		conf.set(GlobalConstants.RUNNING_DATE_PARAMES, date);
	}

	private List<Scan> initScans(Job job) {
		
		Configuration conf = job.getConfiguration();
		String date = conf.get(GlobalConstants.RUNNING_DATE_PARAMES);
		
		long startDate = TimeUtil.parseString2Long(date);
		long endDate = startDate + GlobalConstants.DAY_OF_MILLISECONDS;
		
		Scan scan = new Scan();
		// 设置查询起始行和结束行
		scan.setStartRow(Bytes.toBytes(startDate));
		scan.setStopRow(Bytes.toBytes(endDate));
		
		// 添加过滤器
		FilterList  filterList = new FilterList();
		// 指明要查询的字段
		String[] columns = new String[]{
			EventLogConstants.LOG_COLUMN_NAME_MEMBER_ID, // memberid
			EventLogConstants.LOG_COLUMN_NAME_BROWSER_NAME, // 浏览器名称
			EventLogConstants.LOG_COLUMN_NAME_BROWSER_VERSION, // 浏览器版本
			EventLogConstants.LOG_COLUMN_NAME_PLATFORM, // 平台
			EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME, // servertime
		};
		
		byte[][] prefixes = new byte[columns.length][];
		for(int i =0;i < columns.length;i++){
			prefixes[i] = Bytes.toBytes(columns[i]);
		}
		MultipleColumnPrefixFilter filter = new MultipleColumnPrefixFilter(prefixes);
		filterList.addFilter(filter);
		
		scan.setFilter(filterList);
		
		scan.setAttribute(Scan.SCAN_ATTRIBUTES_TABLE_NAME,Bytes.toBytes(EventLogConstants.HBASE_NAME_EVENT_LOGS));
		
		return Lists.newArrayList(scan);
	}

}
