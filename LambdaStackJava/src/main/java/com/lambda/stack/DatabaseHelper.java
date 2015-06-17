package com.lambda.stack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static org.apache.spark.api.java.JavaRDDLike$class.id;
import static org.apache.spark.api.java.JavaRDDLike$class.name;

/**
 * Created by Henrar on 2015-05-14.
 */
public class DatabaseHelper {

    static final private String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final private String DB_URL = "jdbc:mysql://172.17.84.79/tweetdb";

    static final private String USER = "spark";
    static final private String PASS = "spark";

    public static void init() throws IOException {
        loadStatementStore(DatabaseHelper.class.getClassLoader().getResourceAsStream("ps.store"));
        try{
        DriverManager.registerDriver((Driver)Class.forName(JDBC_DRIVER).newInstance());
        }catch(Exception e) {
            e.printStackTrace();
        }
//        for(String s : statementStore.keySet()){
//            System.out.println(s+" = "+statementStore.get(s));
//        }
    }
    
    private static Map<String,String> statementStore =  new HashMap<>();
    public static String getStatement(String name) {
        return statementStore.get(name);
    }
    private static void loadStatementStore(InputStream in) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder(100);
            String line;
            int hi = -1,sci=-1;
            
            while(true) {
                line = br.readLine();
                if(line==null) break;
                hi = line.indexOf('#');
                if(hi>=0) line = line.substring(0,hi).trim();
                if(line.length() < 1) continue;
                
                sci = line.lastIndexOf(';');
                if(sci>=0){
                    line = line.substring(0,sci);
                    sb.append(line);
                    
                    line = sb.toString();
                    sb.delete(0, sb.length());
                    sci = line.indexOf(':');
                    statementStore.put(line.substring(0,sci), line.substring(sci+1,line.length()));
                }else {
                    sb.append(line);
                }
            }
            
        } finally {
            in.close();
        }
    }
    
    public static DatabaseHelper openDB() throws SQLException {
        return new DatabaseHelper(DriverManager.getConnection(DB_URL, USER, PASS));
    }
    
    private Connection c;
    
//            
//            
//            
//            
//            conn = DriverManager.getConnection(DB_URL, USER, PASS);
//            stmt = conn.createStatement();
//            String sql;
//            sql = "SELECT * FROM TBL_HASHTAG";
//            ResultSet rs = stmt.executeQuery(sql);
//
//            while(rs.next()){
//                System.out.println(rs.getString(1));
//            }
//            rs.close();
//            stmt.close();
//            conn.close();
//        }catch(Exception e ){
//            e.printStackTrace();
//        }finally{
//            try{
//                if(stmt!=null)
//                    stmt.close();
//            }catch(SQLException se){
//                se.printStackTrace();
//            }
//            try{
//                if(conn!=null)
//                    conn.close();
//            }catch(SQLException se){
//                se.printStackTrace();
//            }
//        }
//    }

    public DatabaseHelper(Connection c) {
        this.c = c;
    }
    
    public PreparedStatement createStatement(String name) throws SQLException {
        return c.prepareStatement(statementStore.get(name));
    }
    
    public int findHashTag(String tag) throws SQLException {
        PreparedStatement ps = createStatement("FIND_HASHTAG");
        ps.setString(1, tag);
        
        try(ResultSet rs = ps.executeQuery())
        {
            while(rs.next()){
                return rs.getInt(1);
            }
            return -1;
        }
        
    }
    
    public int findKeyword(String key) throws SQLException {
        PreparedStatement ps = createStatement("FIND_KEYWORD");
        ps.setString(1, key);
        
        try(ResultSet rs = ps.executeQuery())
        {
            while(rs.next()){
                return rs.getInt(1);
            }
            return -1;
        }
        
    }
    
    public List<String> listMonitoredTags() throws SQLException {
        List<String> l = new ArrayList<>();
        PreparedStatement ps = createStatement("MONITORED_HASHTAG");
      
        try(ResultSet rs = ps.executeQuery())
        {
            while(rs.next()){
               l.add(rs.getString(1));
            }
        }
        
        ps.close();
        
        return l;
    }
    
    public int findUser(String twid) throws SQLException {
        PreparedStatement ps = createStatement("FIND_USER");
        ps.setString(1, twid);
        
        try(ResultSet rs = ps.executeQuery())
        {
            while(rs.next()){
                return rs.getInt(1);
            }
            return -1;
        }
        
    }
    
    public int insertUser(String id,String name) throws SQLException {
        PreparedStatement ps = 
                c.prepareStatement(statementStore.get("I_USER"),
                                   PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, id);
        ps.setString(2, name);
        ps.execute();
                
        try(ResultSet rs = ps.getGeneratedKeys()) {
            while(rs.next()){
                return rs.getInt(1);
            }
        } finally {
            ps.close();
        }       
        
        return -1;
    }
    public int insertUserActivity(int user_is,Date date,long count) throws SQLException {
        PreparedStatement ps = 
                c.prepareStatement(statementStore.get("I_USER_ACTIVITY"),
                                   PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setTimestamp(1, new Timestamp(date.getTime()));
        ps.setLong(2,count);
        ps.setInt(3, user_is);
        ps.execute();
                
        try(ResultSet rs = ps.getGeneratedKeys()) {
            while(rs.next()){
                return rs.getInt(1);
            }
        } finally {
            ps.close();
        }       
        
        return -1;
    }
    
    
    public int insertHashTag(String tag) throws SQLException {
        PreparedStatement ps = 
                c.prepareStatement(statementStore.get("I_HASHTAG"),
                                   PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, tag);
        ps.execute();
                
        try(ResultSet rs = ps.getGeneratedKeys()) {
            while(rs.next()){
                return rs.getInt(1);
            }
        } finally {
            ps.close();
        }       
        
        return -1;
    }
    
    public int insertTagActivity(int tagid,Date date,long count) throws SQLException {
        PreparedStatement ps = 
                c.prepareStatement(statementStore.get("I_TAG_ACTIVITY"),
                                   PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setTimestamp(1, new Timestamp(date.getTime()));
        ps.setLong(2,count);
        ps.setInt(3, tagid);
        ps.execute();
                
        try(ResultSet rs = ps.getGeneratedKeys()) {
            while(rs.next()){
                return rs.getInt(1);
            }
        } finally {
            ps.close();
        }       
        
        return -1;
    }
    
    public int insertKeywordUsage(int key,long usage) throws SQLException {
        PreparedStatement ps = 
                c.prepareStatement(statementStore.get("I_KEYWORD_USAGE"),
                                   PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setInt(1, key);
        ps.setLong(2, usage);
        ps.execute();
                
        try(ResultSet rs = ps.getGeneratedKeys()) {
            while(rs.next()){
                return rs.getInt(1);
            }
        } finally {
            ps.close();
        }       
        
        return -1;
    }
    
    public List<Object[]> listKeywords() throws SQLException {
        PreparedStatement ps = createStatement("LIST_KEYWORDS");
        List<Object[]> l = new LinkedList();
        
        try(ResultSet rs = ps.executeQuery())
        {
            while(rs.next()){
                l.add(new Object[]{rs.getString(1),rs.getInt(2)});
            }
           
        }
        ps.close();
        return l;
    }
    
    
    public void close(){
        try{
        c.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
