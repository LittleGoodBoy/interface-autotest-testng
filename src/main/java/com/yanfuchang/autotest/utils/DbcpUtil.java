package com.yanfuchang.autotest.utils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbcp.BasicDataSource;
import com.yanfuchang.autotest.bean.AutoLog;

public class DbcpUtil {
	//初始化
	private static PreparedStatement ps = null;
	//dbcp数据库连接池
	private static BasicDataSource dataSource = new BasicDataSource();
	//通过xml解析工具类获取dbcp连接池所需配置信息
	private static Map<String,String> dbcpConfig = new ParseXmlUtil().getDbcpConfig();
	
	/**
	 * 初始化连接池,避免初始化失败加try-catch
	 */
	static{
		try{
			//连接数据库基础信息
	    	dataSource.setDriverClassName(dbcpConfig.get("DriverClassName"));
	    	dataSource.setUrl(dbcpConfig.get("Url"));
			dataSource.setUsername(dbcpConfig.get("Username"));
			dataSource.setPassword(dbcpConfig.get("Password"));
			
			//连接池配置信息
			dataSource.setInitialSize(Integer.valueOf(dbcpConfig.get("InitialSize")));
			dataSource.setMinIdle(Integer.valueOf(dbcpConfig.get("MinIdle")));
			dataSource.setMaxIdle(Integer.valueOf(dbcpConfig.get("MaxIdle")));
			dataSource.setMaxActive(Integer.valueOf(dbcpConfig.get("MaxActive")));
			
			//连接池借出和客户端返回连接检查配置
			dataSource.setTestOnReturn(Boolean.parseBoolean(dbcpConfig.get("TestOnReturn")));
			dataSource.setTestOnBorrow(Boolean.parseBoolean(dbcpConfig.get("TestOnBorrow")));
			dataSource.setMaxWait(Long.parseLong(dbcpConfig.get("MaxWait")));
			
			//连接池支持预编译
			dataSource.setPoolPreparedStatements(Boolean.parseBoolean(dbcpConfig.get("PoolPreparedStatements")));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 同时刻只能有一个线程从池子借用连接
	 */
	private static synchronized Connection getConnection(){
		Connection con = null;
		try{
			con = dataSource.getConnection();// 从连接池借用连接
		}catch(Exception e){
			e.printStackTrace();
		}
		return con;
	}
	
	/**
	 * 增删改
	 */
	public static int dbcpUpdate(AutoLog autoLog,String sql){
		int count = 0;
		Connection connection = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(sql);
			ps.setString(1, autoLog.getReqType());
			ps.setInt(2, autoLog.getId());
			count = ps.executeUpdate();//执行sql
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(null,ps,connection);
		}
		return count;
	}
	
	/**
	 * 批量增删改
	 */
	public static int[] dbcpBatchUpdate(List<AutoLog> list){
		int[] count = null;
		Connection connection = null;
		try {
			connection = getConnection();
			//从配置信息中获取批量更新的sql语句
			ps = connection.prepareStatement(dbcpConfig.get("BatchSql"));
			for(AutoLog autoLog:list){
				ps.setString(1, autoLog.getTestCase());
			    ps.setString(2, autoLog.getReqType());
			    ps.setString(3, autoLog.getReqUrl());
			    ps.setString(4, autoLog.getReqData());
			    ps.setString(5, autoLog.getExpResult());
			    ps.setString(6, autoLog.getActResult());
			    ps.setInt(7, autoLog.getResult());
			    ps.setString(8, autoLog.getExecTime());
			    ps.addBatch();
			}
			count = ps.executeBatch();//批量插入
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(null,ps,connection);
		}
		return count;
	}
	
	/**
	 * 查询
	 */
	public static List<Object> dbcpQuery(AutoLog autoLog,String sql){
		ResultSet rs = null;
		Connection connection = null;
		List<Object> list = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(sql);
			ps.setInt(1, autoLog.getId());
			rs = ps.executeQuery();
			list = handler(rs,AutoLog.class);//ResultSet结果集处理
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			close(rs,ps,connection);
		}
		return list;
	}
	
	/**
	 * 释放连接  
	 */
	public static void close(ResultSet rs,PreparedStatement ps,Connection connection){
		try {
			if(rs != null){
				rs.close();
			}
			if(ps != null){
				ps.close();
			}
			if(connection != null){
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 利用反射将结果集中的字段名与实体对象中的属性名相对应
	 */
	public static List<Object> handler(ResultSet rs, Class<?> className) {
        List<Object> list = new ArrayList<Object>();
        Object object = null;
        try {
            while (rs.next()) {
                //创建一个className对象实例并将其赋给object
            	object = className.newInstance();
                //获取结果集中的列数
            	ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    // 获取列名
                    String columnName = rsmd.getColumnName(columnIndex);
                    // 对象的属性都是私有的所以要想访问必须加上getDeclaredField(name)
                    Field f = object.getClass().getDeclaredField(columnName);
                    f.setAccessible(true);
                    // 将结果集中的值赋给相应的对象实体的属性
                    f.set(object, rs.getObject(columnName));
                }
                list.add(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
