package com.ibeifeng.offline.data.analysis.platform.stats.mem;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibeifeng.offline.data.analysis.platform.common.DateEnum;
import com.ibeifeng.offline.data.analysis.platform.common.EventLogConstants;
import com.ibeifeng.offline.data.analysis.platform.common.KpiType;
import com.ibeifeng.offline.data.analysis.platform.dimension.BrowserDimension;
import com.ibeifeng.offline.data.analysis.platform.dimension.DateDimension;
import com.ibeifeng.offline.data.analysis.platform.dimension.KpiDimension;
import com.ibeifeng.offline.data.analysis.platform.dimension.PlatformDimension;
import com.ibeifeng.offline.data.analysis.platform.dimension.stats.StatsCommonDimension;
import com.ibeifeng.offline.data.analysis.platform.dimension.stats.StatsUserDimension;
import com.ibeifeng.offline.data.analysis.platform.stats.TimeOutputValue;

/**
 * 活跃会员分析Mapper
 * @author ibeifeng
 *
 */
public class ActiveMemberMapper extends TableMapper<StatsUserDimension, TimeOutputValue>{
	
	private static final Logger logger = LoggerFactory.getLogger(ActiveMemberMapper.class);
	
	private byte[] family = EventLogConstants.BYTES_EVENT_LOGS_FAMILY_NAME;

	private StatsUserDimension outputKey = new StatsUserDimension();
	private TimeOutputValue outputValue = new TimeOutputValue();
	
	private BrowserDimension defaultBrowserDimension = new BrowserDimension("","");
	
	private KpiDimension activeMemberKpi = new KpiDimension(KpiType.ACTIVE_MEMBER.name);
	private KpiDimension browserActiveMemberKpi = new KpiDimension(KpiType.BROWSER_ACTICE_MEMBER.name);
	
	
	@Override
	protected void map(ImmutableBytesWritable key, Result value,
			Context context)
			throws IOException, InterruptedException {
		
		String memberId = Bytes.toString(value.getValue(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_MEMBER_ID)));
		String platform = Bytes.toString(value.getValue(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_PLATFORM)));
		String serverTime =  Bytes.toString(value.getValue(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME)));
		
		if(StringUtils.isBlank(memberId) || StringUtils.isBlank(platform) || 
				StringUtils.isBlank(serverTime) || !StringUtils.isNumeric(serverTime)){
			logger.warn("memberId&platform&serverTime不能为空，且serverTime为时间戳");
			return;
		}
		
		long longOfServerTime = Long.valueOf(serverTime);
		// 构建DateDimension
		DateDimension dateDimension = DateDimension.buildDate(longOfServerTime, DateEnum.DAY);
		
		this.outputValue.setId(memberId);
		
		// 构建PlatformDimension
		List<PlatformDimension> pds = PlatformDimension.buildList(platform);
		
		//构建浏览器维度
		String browserName =  Bytes.toString(value.getValue(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_BROWSER_NAME)));
		String browserVersion =  Bytes.toString(value.getValue(family, Bytes.toBytes(EventLogConstants.LOG_COLUMN_NAME_BROWSER_VERSION)));
		
		List<BrowserDimension> bds = BrowserDimension.buildList(browserName, browserVersion);
		
		// 开始输出
		StatsCommonDimension scd = this.outputKey.getStatsCommon();
		scd.setDate(dateDimension);
		for(PlatformDimension pd :pds){
			this.outputKey.setBrowser(defaultBrowserDimension);
			scd.setPlatform(pd);
			scd.setKpi(activeMemberKpi);
			context.write(outputKey, outputValue);
			
			// 添加浏览器维度
			scd.setKpi(browserActiveMemberKpi);
			for(BrowserDimension bd : bds){
				this.outputKey.setBrowser(bd);
				context.write(outputKey, outputValue);
			}
		}
	}
}
