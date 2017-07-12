package com.ibeifeng.offline.data.analysis.platform.etl;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import com.ibeifeng.offline.data.analysis.platform.common.EventLogConstants;
import com.ibeifeng.offline.data.analysis.platform.common.GlobalConstants;
import com.ibeifeng.offline.data.analysis.platform.util.TimeUtil;


/**
 * 编写mapreduce的runner类
 * 
 * @author ibeifeng
 *
 */
public class AnalyserLogDataRunner implements Tool {
    private static final Logger logger = Logger.getLogger(AnalyserLogDataRunner.class);
    private Configuration conf = null;

    public static void main(String[] args) {
        try {
            int exitcode  = ToolRunner.run(new Configuration(), new AnalyserLogDataRunner(), args);
            if(exitcode == 0){
            	logger.info("任务执行成功！");
            }else{
            	logger.info("任务执行失败！");
            }
        } catch (Exception e) {
            logger.error("执行日志解析job异常", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setConf(Configuration conf) {
    	conf.set("fs.defaultFS", "hdfs://project.ibeifeng.com:8020");
    	conf.set("yarn.resourcemanager.hostname", "project.ibeifeng.com");
    	conf.set("hbase.zookeeper.quorum", "project.ibeifeng.com");
        this.conf = HBaseConfiguration.create(conf);
    }

    @Override
    public Configuration getConf() {
        return this.conf;
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();
        this.processArgs(conf, args);

        Job job = Job.getInstance(conf, "analyser_logdata");

        job.setJarByClass(AnalyserLogDataRunner.class);
        job.setMapperClass(AnalyserLogDataMapper.class);
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Put.class);
        // 设置reducer配置
        // 1. 集群上运行，打成jar运行(要求addDependencyJars参数为true，默认就是true)
        //TableMapReduceUtil.initTableReducerJob(EventLogConstants.HBASE_NAME_EVENT_LOGS, null, job);
        // 2. 本地运行，要求参数addDependencyJars为false
        TableMapReduceUtil.initTableReducerJob(EventLogConstants.HBASE_NAME_EVENT_LOGS, null, job, null, null, null, null, false);
        job.setNumReduceTasks(0);

        // 设置输入路径
        this.setJobInputPaths(job);
        return job.waitForCompletion(true) ? 0 : -1;
    }

    /**
     * 处理参数
     * 
     * @param conf
     * @param args
     */
    private void processArgs(Configuration conf, String[] args) {
        String date = null;
        for (int i = 0; i < args.length; i++) {
            if ("-d".equals(args[i])) {
                if (i + 1 < args.length) {
                    date = args[++i];
                    break;
                }
            }
        }

        // 要求date格式为: yyyy-MM-dd
        if (StringUtils.isBlank(date) || !TimeUtil.isValidateRunningDate(date)) {
            // date是一个无效时间数据
            date = TimeUtil.getYesterday(); // 默认时间是昨天
            System.out.println(date);
        }
        conf.set(GlobalConstants.RUNNING_DATE_PARAMES, date);
    }

    /**
     * 设置job的输入路径
     * 
     * @param job
     */
    private void setJobInputPaths(Job job) {
        Configuration conf = job.getConfiguration();
        FileSystem fs = null;
        try {
            fs = FileSystem.get(conf);
            String date = conf.get(GlobalConstants.RUNNING_DATE_PARAMES);
            //System.out.println(date);
//            Path inputPath = new Path("/flume/" + TimeUtil.parseLong2String(TimeUtil.parseString2Long(date), "MM/dd/"));
            Path inputPath = new Path("/flume/" + TimeUtil.parseLong2String(TimeUtil.parseString2Long(date), "yyyyMMdd"));
            if (fs.exists(inputPath)) {
                FileInputFormat.addInputPath(job, inputPath);
            } else {
                throw new RuntimeException("文件不存在:" + inputPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("设置job的mapreduce输入路径出现异常", e);
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    // nothing
                }
            }
        }
    }
}
