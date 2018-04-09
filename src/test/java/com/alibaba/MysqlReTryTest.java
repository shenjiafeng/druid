package com.alibaba;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

public class MysqlReTryTest extends TestCase {

    private String          jdbcUrl;
    private String          user;
    private String          password;
    private String          driverClass;

    private DruidDataSource dataSource;

    private CountDownLatch countDownLatch = new CountDownLatch(200);

    protected void setUp() throws Exception {
        jdbcUrl = "jdbc:mysql://127.0.0.1:3306/Test";
        user = "root";
        password = "Mcrl74110";
        driverClass = "com.mysql.jdbc.Driver";

        dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setMaxActive(6);
        dataSource.setMaxWait(1000);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(20);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        //dataSource.setTestWhileIdle(true);
    }


    public void test_0() throws Exception{

        ExecutorService xd = Executors.newFixedThreadPool(200);

        int i = 1;

        while (true) {
            xd.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        Connection conn = dataSource.getConnection();

                        String sql = "SELECT INSERT('Quadratic', 1, 4, 'What')";

                        PreparedStatement stmt = conn.prepareStatement(sql);

                        ResultSet rs = stmt.executeQuery();
                        JdbcUtils.printResultSet(rs);

                        //rs.close();
                        //stmt.close();
                        //conn.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
            Thread.sleep(10);
        }

        //LockSupport.park();


    }
}
