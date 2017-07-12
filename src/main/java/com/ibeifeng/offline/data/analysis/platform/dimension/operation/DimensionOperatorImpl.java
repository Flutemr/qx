package com.ibeifeng.offline.data.analysis.platform.dimension.operation;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibeifeng.offline.data.analysis.platform.dimension.BaseDimension;
import com.ibeifeng.offline.data.analysis.platform.dimension.BrowserDimension;
import com.ibeifeng.offline.data.analysis.platform.dimension.DateDimension;
import com.ibeifeng.offline.data.analysis.platform.dimension.PlatformDimension;
import com.mysql.jdbc.Statement;

public class DimensionOperatorImpl implements IDimensionOperator{
	
	private static final Logger logger = LoggerFactory.getLogger(DimensionOperatorImpl.class);
	
	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static final String URL = "jdbc:mysql://project.ibeifeng.com:3306/test";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "123456";
	
	private ThreadLocal<Connection> localConn = new ThreadLocal<Connection>();
	
	private Map<String,Integer> cache = new LinkedHashMap<String,Integer>(){

		/**
		 * 
		 */
		private static final long serialVersionUID = -3084359201061689731L;

		@Override
		protected boolean removeEldestEntry(Entry<String, Integer> eldest) {
			// 缓存容量
			return this.size() > 5000;
		}
	};
	
	static {
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private Connection getConnection() throws SQLException{
		Connection conn = null;
		synchronized (this) {
			conn = localConn.get();
			try {
				if(conn == null || conn.isClosed() || !conn.isValid(3)){
					conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				
				try {
					if(conn != null) conn.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			}
			this.localConn.set(conn);
		}
		return conn;
	}
	
	public DimensionOperatorImpl() {
		// 添加关闭的钩子，jvm关闭时，会触发该线程执行
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
			@Override
			public void run() {
				logger.info("开始关闭数据库......");
				Connection conn = localConn.get();
				if(conn != null){
					try {
						conn.close();
					} catch (SQLException e) {
					}
				}
				logger.info("关闭数据库成功！");
			}
		}));
	}

	/**
	 * 根据值获取ID 即先根据值到mysql数据表中查询出记录，如果存在记录，则直接取id，如果没有则先插入一条新的记录然后在取id
	 */
	@Override
	public int getDimensionIDByValue(BaseDimension value) throws IOException {
		String cacheKey = DimensionOperatorImpl.buildCacheKey(value); // 获取cache
		// key
		if (this.cache.containsKey(cacheKey)) {
			return this.cache.get(cacheKey);
		}

		Connection conn = null;
		try {
			// 1. 查看数据库中是否有对应的值，有则返回
			// 2. 如果第一步中，没有值；先插入我们dimension数据， 获取id
			String[] sql = null; // 具体执行sql数组
			if (value instanceof DateDimension) {
				sql = this.buildDateSql();
			} else if (value instanceof PlatformDimension) {
				sql = this.buildPlatformSql();
			} else if (value instanceof BrowserDimension) {
                sql = this.buildBrowserSql();
            } else {
				throw new IOException("不支持此dimensionid的获取:" + value.getClass());
			}

			conn = this.getConnection(); // 获取连接
			int id = 0;
			synchronized (this) {
				id = this.executeSql(conn, cacheKey, sql, value);
			}
			return id;
		} catch (Throwable e) {
			logger.error("操作数据库出现异常", e);
			throw new IOException(e);
		}

	}
	
	/**
     * 创建cache key
     * 
     * @param dimension
     * @return
     */
    public static String buildCacheKey(BaseDimension dimension) {
    	StringBuilder sb = new StringBuilder();
        if (dimension instanceof DateDimension) {
            sb.append("date_dimension");
            DateDimension date = (DateDimension) dimension;
            sb.append(date.getYear()).append(date.getSeason()).append(date.getMonth());
            sb.append(date.getWeek()).append(date.getDay()).append(date.getType());
        } else if (dimension instanceof PlatformDimension) {
            sb.append("platform_dimension");
            PlatformDimension platform = (PlatformDimension) dimension;
            sb.append(platform.getPlatformName());
        } else if (dimension instanceof BrowserDimension) {
            sb.append("browser_dimension");
            BrowserDimension browser = (BrowserDimension) dimension;
            sb.append(browser.getBrowser()).append(browser.getBrowserVersion());
        }

        if (sb.length() == 0) {
            throw new RuntimeException("无法创建指定dimension的cachekey：" + dimension.getClass());
        }
        return sb.toString();
    }

    /**
     * 设置参数
     * 
     * @param pstmt
     * @param dimension
     * @throws SQLException
     */
    private void setArgs(PreparedStatement pstmt, BaseDimension dimension) throws SQLException {
        int i = 0;
        if (dimension instanceof DateDimension) {
            DateDimension date = (DateDimension) dimension;
            pstmt.setInt(++i, date.getYear());
            pstmt.setInt(++i, date.getSeason());
            pstmt.setInt(++i, date.getMonth());
            pstmt.setInt(++i, date.getWeek());
            pstmt.setInt(++i, date.getDay());
            pstmt.setString(++i, date.getType());
            pstmt.setDate(++i, new Date(date.getCalendar().getTime()));
        } else if (dimension instanceof PlatformDimension) {
            PlatformDimension platform = (PlatformDimension) dimension;
            pstmt.setString(++i, platform.getPlatformName());
        } else if (dimension instanceof BrowserDimension) {
            BrowserDimension browser = (BrowserDimension) dimension;
            pstmt.setString(++i, browser.getBrowser());
            pstmt.setString(++i, browser.getBrowserVersion());
        }
    }

    /**
     * 创建date dimension相关sql
     * 
     * @return
     */
    private String[] buildDateSql() {
        String querySql = "SELECT `id` FROM `dimension_date` WHERE `year` = ? AND `season` = ? AND `month` = ? AND `week` = ? AND `day` = ? AND `type` = ? AND `calendar` = ?";
        String insertSql = "INSERT INTO `dimension_date`(`year`, `season`, `month`, `week`, `day`, `type`, `calendar`) VALUES(?, ?, ?, ?, ?, ?, ?)";
        return new String[] { querySql, insertSql };
    }

    /**
     * 创建polatform dimension相关sql
     * 
     * @return
     */
    private String[] buildPlatformSql() {
        String querySql = "SELECT `id` FROM `dimension_platform` WHERE `platform_name` = ?";
        String insertSql = "INSERT INTO `dimension_platform`(`platform_name`) VALUES(?)";
        return new String[] { querySql, insertSql };
    }
    
    /**
     * 创建browser dimension相关sql
     * 
     * @return
     */
    private String[] buildBrowserSql() {
        String querySql = "SELECT `id` FROM `dimension_browser` WHERE `browser_name` = ? AND `browser_version` = ?";
        String insertSql = "INSERT INTO `dimension_browser`(`browser_name`, `browser_version`) VALUES(?, ?)";
        return new String[] { querySql, insertSql };
    }

    /**
     * 具体执行sql的方法
     * 
     * @param conn
     * @param cacheKey
     * @param sqls
     * @param dimension
     * @return
     * @throws SQLException
     */
    @SuppressWarnings("resource")
    private int executeSql(Connection conn, String cacheKey, String[] sqls, BaseDimension dimension) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sqls[0]); // 创建查询sql的pstmt对象
            // 设置参数
            this.setArgs(pstmt, dimension);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // 返回值
            }
            // 代码运行到这儿，表示该dimension在数据库中不存储，进行插入
            pstmt = conn.prepareStatement(sqls[1], Statement.RETURN_GENERATED_KEYS);
            // 设置参数
            this.setArgs(pstmt, dimension);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys(); // 获取返回的自动生成的id
            if (rs.next()) {
                return rs.getInt(1); // 获取返回值
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Throwable e) {
                    // nothing
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (Throwable e) {
                    // nothing
                }
            }
        }
        throw new RuntimeException("从数据库获取id失败");
    }

}
