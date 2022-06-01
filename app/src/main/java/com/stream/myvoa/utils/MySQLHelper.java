package com.stream.myvoa.utils;

import android.util.Log;

import com.stream.myvoa.bean.VOAObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLHelper {
    private static final String url = "jdbc:mysql://服务器IP:3306/voa?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=GMT&allowPublicKeyRetrieval=true";
    //数据库用户名
    private static final String user = "username";
    //数据库密码
    private static final String password = "password";
    //驱动程序类
    private static String driverClass = "com.mysql.cj.jdbc.Driver";

    private static final String TAG = "MySQLHelper";
    public static Connection connection;
    public static Statement statement;
    private static PreparedStatement preparedStatement;
    public static ResultSet result;
    public static String sql;

    /**
     * 从服务器数据库获取VOA资源，包括标题、MP3链接、LRC链接
     * @return VOAList
     * @throws SQLException
     * @throws InterruptedException
     */
    public static ArrayList<VOAObject> getVOAList() throws SQLException, InterruptedException {
        ArrayList<VOAObject> voaList = new ArrayList<>();
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connection = MySQLHelper.getConnection();
                    statement = connection.createStatement();
                    sql = "select title,content,mp3_url,lrc_path from voas";
                    result = statement.executeQuery(sql);
                    while (result.next()){
                        Log.e("MySQLHelper", result.getString("title"));
                        Log.e("MySQLHelper", result.getString("mp3_url"));
                        Log.e("MySQLHelper", result.getString("lrc_path"));
                        voaList.add(new VOAObject(result.getString("title"),result.getString("content"),
                                result.getString("mp3_url"),result.getString("lrc_path")));
                    }
                    connection.close();
                }catch (SQLException throwable) {
                    throwable.printStackTrace();
                }
            }
        });
        thread.start();
        while(true) {
            try {
                thread.join(); //等待数据全部写入到voaList后才返回
                break;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return voaList;
    }

    static {
        try {
            Class.forName(driverClass).newInstance();
            Log.e(TAG,"加载JDBC驱动成功");
        } catch (Exception e) {
            Log.e(TAG,"加载JDBC驱动失败");
            e.printStackTrace();
        }
    }

    /**
     * 测试
     * @return
     * @throws SQLException
     */
    public static ArrayList<String> test() throws SQLException, InterruptedException {
        ArrayList<String> voaList = new ArrayList<String>();
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connection = MySQLHelper.getConnection();
                    statement = connection.createStatement();
                    sql = "select title from voas";
                    result = statement.executeQuery(sql);
                    while (result.next()){
                        Log.e(TAG, "MySQL connect test: "+result.getString("title"));;
                        voaList.add(result.getString("title"));
                    }
                    connection.close();
                }catch (SQLException throwable) {
                    throwable.printStackTrace();
                }
            }
        });
        thread.start();
        while(true) {
            try {
                thread.join(); //等待数据全部写入到voaList后才返回
                break;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return voaList;
    }

    /**
     * 获取连接方法
     */
    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            Log.e(TAG,"数据库连接成功！");
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG,"数据库连接失败！");
            throw new RuntimeException(e);
        }
    }

    /**
     * 释放资源的方法
     */
    public static void close(Statement stmt, Connection conn) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 释放资源的方法
     */
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
