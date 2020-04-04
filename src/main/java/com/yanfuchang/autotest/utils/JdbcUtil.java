package com.yanfuchang.autotest.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import com.yanfuchang.autotest.bean.AutoLog;

/**
 * 数据库操作工具类,用于保存执行测试用例后的结果
 */
public class JdbcUtil {
	
	private static Properties properties;
	private static String url;
	private static String user;
	private static String password;
	
	/**
	 * 读取配置文件,初始化配置信息
	 */
	static {
		properties = new Properties();
		InputStream in = ClassLoader.getSystemResourceAsStream("jdbc-config.properties");
		try {
			properties.load(in);//加载配置文件
			Class.forName(properties.getProperty("jdbc.driver"));
			url = properties.getProperty("jdbc.url");
			user = properties.getProperty("jdbc.user");
			password = properties.getProperty("jdbc.password");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取数据库连接对象
	 * @return
	 */
	public static Connection getConnection() {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	/**
	 * 增删改
	 * @param autoLog
	 * @param sql
	 * @return
	 */
	public static int jdbcUpdate(AutoLog autoLog,String sql){
		//初始化
		int count = 0;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, autoLog.getReqType());
			ps.setInt(2, autoLog.getId());
			//执行select sql
			count = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn,ps,null);
		}
		return count;
	}
	
	/**
	 * 批量增删改
	 * @param list
	 * @param sql
	 * @return
	 */
	public static int[] jdbcBatchUpdate(List<AutoLog> list,String sql){
		//初始化
		int[] count = null;
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(sql);
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
			//批量插入
			count = ps.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn,ps,null);
		}
		return count;
	}
	
	/**
	 * jdbc直连的方式查询数据库
	 * @param autoLog
	 * @param sql
	 * @return
	 */
	public static List<Object> jdbcQuery(AutoLog autoLog,String sql){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		List<Object> data = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, autoLog.getReqType());//根据类型查询
			res = ps.executeQuery();
			data = handler(res,AutoLog.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(conn,ps,res);
		}
		return data;
	}
	/**
	 * 处理查询结果集,将结果集中的数据封装到list中返回
	 * @param res
	 * @param class1
	 * @return
	 */
	private static List<Object> handler(ResultSet res, Class<?> className) {
		List<Object> list = new ArrayList<>();
		Object obj = null;
		try {
			//判断结果集中是否还有下一条数据,如果有则进入循环,没有则结束循环
			while (res.next()) {
				obj = className.newInstance();
				ResultSetMetaData rsmd = res.getMetaData();
				int columnCount = rsmd.getColumnCount();//获取总列数
				//循环获取每一列的列名称,从结果集中获取相应的数据,然后给对象相同字段名称复制
				for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
					String columnName = rsmd.getColumnName(columnIndex);//获取每一列的列名称
					Field field = obj.getClass().getDeclaredField(columnName);//反射获取指定名称的类中的字段
					field.setAccessible(true);//设置字段可访问
					field.set(obj, res.getObject(columnName));
				}
				list.add(obj);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 释放连接
	 * @param conn
	 * @param ps
	 * @param res
	 */
	private static void close(Connection conn, PreparedStatement ps, ResultSet res) {
		try {
			if(conn != null) {
				conn.close();
			}
			if(ps != null) {
				ps.close();
			}
			if(res != null) {
				res.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		//测试单条修改
		String sql2 = "update AutoLog set reqType = ? where id = ?";
		AutoLog log2 = new AutoLog();
		log2.setId(1);
		log2.setReqType("get");
		int jdbcUpdate = jdbcUpdate(log2,sql2);
		if(jdbcUpdate == 1) {
			System.out.println("修改成功");
		}else {
			System.out.println("修改失败");
		}
		
		//测试批量插入
		AutoLog log = new AutoLog();
		log.setActResult("1");
		log.setExecTime("1");
		log.setExpResult("1");
		log.setReqData("1");
		log.setReqType("1");
		log.setReqUrl("1");
		log.setResult(1);
		log.setTestCase("测试");
		List<AutoLog> list = new ArrayList<>();
		list.add(log);
		String sql = "insert into AutoLog(testCase,reqType,reqUrl,reqData,expResult,actResult,result,execTime) values(?,?,?,?,?,?,?,?)";
		int[] jdbcBatchUpdate = jdbcBatchUpdate(list,sql);
		System.out.println(jdbcBatchUpdate[0]);
		
		//测试根据请求类型查询所有测试历史记录
		AutoLog log3 = new AutoLog();
		log3.setReqType("get");
		String sql3 = "select * from AutoLog where reqType = ?";
		List<Object> jdbcQuery = jdbcQuery(log3,sql3);
		for (int i = 0; i < jdbcQuery.size(); i++) {
			System.out.println(jdbcQuery.get(i));
		}
	}
	
}
