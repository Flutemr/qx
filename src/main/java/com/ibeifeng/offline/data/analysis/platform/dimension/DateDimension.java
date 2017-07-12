package com.ibeifeng.offline.data.analysis.platform.dimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import com.ibeifeng.offline.data.analysis.platform.common.DateEnum;
import com.ibeifeng.offline.data.analysis.platform.util.TimeUtil;

/**
 * 鏃ユ湡缁村害
 * @author ibeifeng
 *
 */
public class DateDimension extends BrowserDimension{
	
	private int id;
	private int year;
	private int season;
	private int month;
	private int week;
	private int day;
	private String type;
	private Date calendar = new Date();
	
	/**
	 * 鏋勯�鏃ユ湡缁村害瀵硅薄
	 * @param time
	 * @param type
	 * @return
	 */
	public static DateDimension buildDate(long time,DateEnum type) {
		int year = TimeUtil.getDateInfo(time, DateEnum.YEAR);
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		if (DateEnum.YEAR.equals(type)) {
			calendar.set(year, 0, 1);
			return new DateDimension(year, 0, 0, 0, 0, type.name, calendar.getTime());
		}
		int season = TimeUtil.getDateInfo(time, DateEnum.SEASON);
		if (DateEnum.SEASON.equals(type)) {
			int month = (3 * season - 2);
			calendar.set(year, month - 1, 1);
			return new DateDimension(year, season, 0, 0, 0, type.name, calendar.getTime());
		}
		int month = TimeUtil.getDateInfo(time, DateEnum.MONTH);
		if (DateEnum.MONTH.equals(type)) {
			calendar.set(year, month - 1, 1);
			return new DateDimension(year, season, month, 0, 0, type.name, calendar.getTime());
		}
		int week = TimeUtil.getDateInfo(time, DateEnum.WEEK);
		if (DateEnum.WEEK.equals(type)) {
			long firstDayOfWeek = TimeUtil.getFirstDayOfThisWeek(time); // 鑾峰彇鎸囧畾鏃堕棿鎴虫墍灞炲懆鐨勭涓�ぉ鏃堕棿鎴�
			year = TimeUtil.getDateInfo(firstDayOfWeek, DateEnum.YEAR);
			season = TimeUtil.getDateInfo(firstDayOfWeek, DateEnum.SEASON);
			month = TimeUtil.getDateInfo(firstDayOfWeek, DateEnum.MONTH);
			week = TimeUtil.getDateInfo(firstDayOfWeek, DateEnum.WEEK);
			if (month == 12 && week == 1) {
				week = 53;
			}
			return new DateDimension(year, season, month, week, 0, type.name, new Date(firstDayOfWeek));
		}
		int day = TimeUtil.getDateInfo(time, DateEnum.DAY);
		if (DateEnum.DAY.equals(type)) {
			calendar.set(year, month - 1, day);
			if (month == 12 && week == 1) {
				week = 53;
			}
			return new DateDimension(year, season, month, week, day, type.name, calendar.getTime());
		}
		throw new RuntimeException("涓嶆敮鎸佹墍瑕佹眰鐨刣ateEnum绫诲瀷鏉ヨ幏鍙杁atedimension瀵硅薄" + type);
	}


	public DateDimension() {
		super();
	}

	public DateDimension(int id, int year, int season, int month, int week, int day, String type, Date calendar) {
		this(year,season,month,week,day,type,calendar);
		this.id = id;
	}

	public DateDimension(int year, int season, int month, int week, int day, String type, Date calendar) {
		this(year,season,month,week,day,type);
		this.calendar = calendar;
	}
	
	public DateDimension(int year, int season, int month, int week, int day, String type) {
		super();
		this.year = year;
		this.season = season;
		this.month = month;
		this.week = week;
		this.day = day;
		this.type = type;
	}


	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(this.id);
		out.writeInt(this.year);
		out.writeInt(this.season);
		out.writeInt(this.month);
		out.writeInt(this.week);
		out.writeInt(this.day);
		out.writeUTF(this.type);
		out.writeLong(this.calendar.getTime());
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.id = in.readInt();
		this.year = in.readInt();
		this.season = in.readInt();
		this.month = in.readInt();
		this.week = in.readInt();
		this.day = in.readInt();
		this.type = in.readUTF();
		this.calendar.setTime(in.readLong());
	}

	@Override
	public int compareTo(BaseDimension o) {
		if(o == this) return 0;
		
		DateDimension dd = (DateDimension) o;
		int tmp = Integer.compare(this.id, dd.getId());
		if(tmp != 0) return tmp;
		
		tmp = Integer.compare(this.year, dd.getYear());
		if(tmp != 0 ) return tmp;
		
		tmp = Integer.compare(this.season, dd.getSeason());
		if(tmp!= 0) return tmp;
		
		tmp = Integer.compare(this.month, dd.getMonth());
		if(tmp!= 0) return tmp;
		
		tmp = Integer.compare(this.week, dd.getWeek());
		if(tmp!=0) return tmp;
		
		tmp = Integer.compare(this.day, dd.getDay());
		if(tmp != 0) return tmp;
		
		return this.type.compareTo(dd.getType());
	}
	
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getSeason() {
		return season;
	}

	public void setSeason(int season) {
		this.season = season;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCalendar() {
		return calendar;
	}

	public void setCalendar(Date calendar) {
		this.calendar = calendar;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((calendar == null) ? 0 : calendar.hashCode());
		result = prime * result + day;
		result = prime * result + id;
		result = prime * result + month;
		result = prime * result + season;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + week;
		result = prime * result + year;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DateDimension other = (DateDimension) obj;
		if (calendar == null) {
			if (other.calendar != null)
				return false;
		} else if (!calendar.equals(other.calendar))
			return false;
		if (day != other.day)
			return false;
		if (id != other.id)
			return false;
		if (month != other.month)
			return false;
		if (season != other.season)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (week != other.week)
			return false;
		if (year != other.year)
			return false;
		return true;
	}
}
