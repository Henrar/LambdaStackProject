package com.lambda.stack;

import java.sql.*;

/**
 * Created by Henrar on 2015-05-14.
 */
public class DatabaseHelper {

    static final private String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final private String DB_URL = "jdbc:mysql://172.17.84.79/tweetdb";

    static final private String USER = "spark";
    static final private String PASS = "spark";

    public void connectToDatabase()
    {
        Connection conn = null;
        Statement stmt = null;
        try{
            
            
            DriverManager.registerDriver((Driver)Class.forName(JDBC_DRIVER).newInstance());
            
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT * FROM TBL_HASHTAG";
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                System.out.println(rs.getString(1));
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
