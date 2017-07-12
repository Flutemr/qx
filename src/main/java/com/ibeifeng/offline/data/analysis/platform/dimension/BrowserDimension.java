package com.ibeifeng.offline.data.analysis.platform.dimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ibeifeng.offline.data.analysis.platform.common.GlobalConstants;
/**
 * 浏览器维度（浏览器名称和浏览器版本号）
 * @author ibeifeng
 *
 */
public class BrowserDimension extends BaseDimension{
	
	private int id;
	private String browser;
	private String browserVersion;
	
	public BrowserDimension() {
		super();
	}
	
	public BrowserDimension(String browser, String browserVersion) {
		super();
		this.browser = browser;
		this.browserVersion = browserVersion;
	}
	
	public void clean(){
		this.id = 0;
		this.browser = "";
		this.browserVersion = "";
	}
	
	public static BrowserDimension newInstance(String browser,String browserVersion){
		BrowserDimension bd = new BrowserDimension();
		bd.setBrowser(browser);
		bd.setBrowserVersion(browserVersion);
		return bd;
	}
	/**
	 * 构造多个浏览器维度的对象集合
	 * @param browser
	 * @param browserVersion
	 * @return
	 */
	public static List<BrowserDimension> buildList(String browser,String browserVersion){
		if(StringUtils.isBlank(browser)){
			browser = GlobalConstants.DEFAULT_VALUE;
			browserVersion = GlobalConstants.DEFAULT_VALUE;
		}
		if(StringUtils.isBlank(browserVersion)){
			browserVersion = GlobalConstants.DEFAULT_VALUE;
		}
		List<BrowserDimension> bds = new ArrayList<BrowserDimension>();
		// 不区分版本
		bds.add(BrowserDimension.newInstance(browser, GlobalConstants.VALUE_OF_ALL));
		bds.add(BrowserDimension.newInstance(browser, browserVersion));
		return bds;
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(id);
		out.writeUTF(browser);
		out.writeUTF(browserVersion);
	}
	@Override
	public void readFields(DataInput in) throws IOException {
		this.id = in.readInt();
		this.browser = in.readUTF();
		this.browserVersion = in.readUTF();
	}
	@Override
	public int compareTo(BaseDimension o) {
		if(o == this) return 0;
		BrowserDimension bd = (BrowserDimension) o;
		int result = Integer.compare(this.id, bd.getId());
		if(result == 0){
			int result1 = this.browser.compareTo(bd.getBrowser());
			if(result1 == 0){
				return this.browserVersion.compareTo(bd.getBrowserVersion());
			}
			return result1;
		}
		return result;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getBrowserVersion() {
		return browserVersion;
	}

	public void setBrowserVersion(String browserVersion) {
		this.browserVersion = browserVersion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((browser == null) ? 0 : browser.hashCode());
		result = prime * result + ((browserVersion == null) ? 0 : browserVersion.hashCode());
		result = prime * result + id;
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
		BrowserDimension other = (BrowserDimension) obj;
		if (browser == null) {
			if (other.browser != null)
				return false;
		} else if (!browser.equals(other.browser))
			return false;
		if (browserVersion == null) {
			if (other.browserVersion != null)
				return false;
		} else if (!browserVersion.equals(other.browserVersion))
			return false;
		if (id != other.id)
			return false;
		return true;
	}
}
