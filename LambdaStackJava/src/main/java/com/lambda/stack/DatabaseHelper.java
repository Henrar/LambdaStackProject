package com.lambda.stack;

import java.sql.*;

/**
 * Created by Henrar on 2015-05-14.
 */
public class DatabaseHelper {

    static final private String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final private String DB_URL = "jdbc:mysql:172.17.84.79";

    static final private String USER = "spark";
    static final private String PASS = "spark";

    public void connectToDatabase()
    {
        Connection conn = null;
        Statement stmt = null;
        try{
            Class.forName("mysql-connector-java-5.1.35.jar");

            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT FLD_ID, FLD_MESSAGE, FLD_COUNT FROM TWEET_COUNT";
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                int id = rs.getInt("FLD_ID");
                String msg = rs.getString("FLD_MESSAGE");
                int count = rs.getInt("FLD_COUNT");

                System.out.println(id + " " + msg + " " + count);
            }
            rs.close();
            stmt.close();
            conn.close();
        }catch(Exception e ){
            e.printStackTrace();
        }finally{
            try{
                if(stmt!=null)
                    stmt.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
    }
}
