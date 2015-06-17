/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.agh.iosr.lsf.dao;

import com.sun.java.swing.plaf.windows.WindowsTreeUI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.edu.agh.iosr.lsf.QueryExecutor;

/**
 *
 * @author Dariusz Hudziak
 */
@Service
public class DatabaseHelper {
    
    static {
        try{
        init();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    private static Map<String,String> statementStore;
    public static String getStatement(String name) {
        return statementStore.get(name);
    }
    public static List<String> getTestStatemets(){
        List<String> ls =  new LinkedList<>();
        
        for(String s : statementStore.keySet()){
            if(s.startsWith("T_")){
                ls.add(s);
            }
        }
        
        return ls;
    } 
    public static List<String> loadWords(){
        try{
            return loadWords(DatabaseHelper.class.getClassLoader().getResourceAsStream("words.txt"));
        }catch(IOException e){
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
    private static List<String> loadWords(InputStream in) throws IOException {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(in)))
        {
        List<String> words = new ArrayList<>();
        
        String line;
         while(true) {
                line = br.readLine();
                if(line==null) break;
                words.add(line);
         }
        
        return words;
        }
    }
    private static Map<String,String> loadStatementStore(InputStream in) throws IOException 
    {
        Map<String,String> statementStore =  new HashMap<>();
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
        return statementStore;
    }
    public static void init() throws IOException {
       statementStore = loadStatementStore(DatabaseHelper.class.getClassLoader().getResourceAsStream("ps.store"));
    }
    
    private DataSource source;
    
    @Autowired
    @Qualifier("dataSource")
    public void setSource(DataSource ds) {
        source = ds;
    }
    
    public long benhmarkStatement(String name) throws SQLException {
        try(Connection c = source.getConnection()) {
            PreparedStatement ps = c.prepareStatement(getStatement(name));
            return benchmark(ps);
        }
    }
    
    public List<String[]> selectStatement(String name) throws SQLException {
        return select(getStatement(name));
    }
    public List<String[]> select(String sql) throws SQLException {
        try(Connection c = source.getConnection()) {
            PreparedStatement ps = c.prepareStatement(sql);
            return select(ps);
        }
    }
    public List<String[]> select(PreparedStatement ps) throws SQLException {
        List<String[]> result = new LinkedList();
        
      
        try(ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData rsmd = rs.getMetaData();
            int col = rsmd.getColumnCount();
            
            while(rs.next()){
                String[] row = new String[col];
                for(int i=0;i<row.length;++i) {
                    row[i] = rs.getString(i+1);
                }
                result.add(row);
            }
            
        } finally {
            ps.close();
        }
       
        return result;
    }
    public long benchmark(PreparedStatement ps) throws SQLException {
        long start = System.currentTimeMillis();
        
        try(ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData rsmd = rs.getMetaData();
            int col = rsmd.getColumnCount();
            
            while(rs.next()){
                String[] row = new String[col];
                for(int i=0;i<row.length;++i) {
                    row[i] = rs.getString(i+1);
                }
            }
            
        } finally {
            ps.close();
        }
       
        return System.currentTimeMillis()-start;
    }
    public int insert(PreparedStatement ps) throws SQLException {
        ps.execute();
        try(ResultSet rs = ps.getGeneratedKeys()){
            while(rs.next()){
                return rs.getInt(1);
            }
        }
        return -1;
    }
    
    public boolean tagKeywordUsage(String name) throws SQLException {
        try(Connection c = source.getConnection() ) {
            PreparedStatement ps = c.prepareStatement(statementStore.get("FIND_KEYWORD_TU"));
            ps.setString(1, name);
            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        }
    }
    public List<String[]> listKeywords() throws SQLException {
        try(Connection c = source.getConnection() ) {
            PreparedStatement ps = c.prepareStatement(statementStore.get("LIST_KEYWORDS"));
            return select(ps);
        }
    }
    public int insertKeyword(String key,String cat) throws SQLException {
        try(Connection c = source.getConnection()) {
            PreparedStatement ps = c.prepareStatement(getStatement("FIND_CATEGORY"));
            ps.setString(1, cat);
            List<String[]> res = select(ps);
            int id=-1;
            
            if(res.size()>0){
                id = Integer.parseInt(res.get(0)[0]);
            }
            if(id==-1){
                ps = c.prepareStatement(getStatement("I_CATEGORY"),PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, cat);
                id = insert(ps);
            }
            
            ps = c.prepareStatement(getStatement("I_KEYWORD"),PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, key);
            ps.setInt(2, id);
            return insert(ps);
        }
    }
    public void deleteKeyword(String key) throws SQLException {
        try(Connection c = source.getConnection()){
            PreparedStatement ps = c.prepareStatement(getStatement("D_KEYWORD"));
            ps.setString(1, key);
            ps.execute();
        }
    }
    
    public List<String[]> listTags() throws SQLException {
        try(Connection c = source.getConnection() ) {
            LinkedList<String[]> r = new LinkedList<>();
            PreparedStatement ps = c.prepareStatement(statementStore.get("LIST_TAGS"));
            ps.setInt(1, 1);
            r.addAll(select(ps));
            ps = c.prepareStatement(getStatement("LIST_TAGS"));
            ps.setInt(1, 0);
            r.addAll(select(ps));
            return r;
        }
    }
    public List<String[]> listMonitoredTags() throws SQLException {
        try(Connection c = source.getConnection()){
            PreparedStatement ps = c.prepareStatement(statementStore.get("LIST_TAGS"));
            ps.setInt(1, 1);
            return select(ps);
        }
    }
    public void monitorTag(String tag) throws SQLException {
        try(Connection c = source.getConnection()){
            PreparedStatement ps = c.prepareStatement(statementStore.get("U_TAGS"));
            ps.setString(1, tag);
            ps.execute();
        }
    }
    public List<String[]> listTagsActivity(Timestamp from,Timestamp to) throws SQLException {
        try(Connection c = source.getConnection()){
            PreparedStatement ps = c.prepareStatement(statementStore.get("LIST_TAG_ACTIVITY"));
            ps.setTimestamp(1, from);
            ps.setTimestamp(2, to);
            return select(ps);
        }
    }
    
    public List<String[]> listUsers() throws SQLException {
        try(Connection c = source.getConnection() ) {
            LinkedList<String[]> r = new LinkedList<>();
            PreparedStatement ps = c.prepareStatement(statementStore.get("LIST_USERS"));
            ps.setInt(1, 1);
            r.addAll(select(ps));
            ps = c.prepareStatement(getStatement("LIST_USERS"));
            ps.setInt(1, 0);
            r.addAll(select(ps));
            return r;
        }
    }
    public List<String[]> listMonitoredUsers() throws SQLException {
        try(Connection c = source.getConnection()){
            PreparedStatement ps = c.prepareStatement(statementStore.get("LIST_USERS"));
            ps.setInt(1, 1);
            return select(ps);
        }
    }
    public void monitorUser(String user) throws SQLException {
        try(Connection c = source.getConnection()){
            PreparedStatement ps = c.prepareStatement(statementStore.get("U_USERS"));
            ps.setString(1, user);
            ps.execute();
        }
    }
    public List<String[]> listUsersActivity(Timestamp from,Timestamp to) throws SQLException {
        try(Connection c = source.getConnection()){
            PreparedStatement ps = c.prepareStatement(statementStore.get("LIST_USER_ACTIVITY"));
            ps.setTimestamp(1, from);
            ps.setTimestamp(2, to);
            return select(ps);
        }
    }
    
    public String regenerateDB(){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        
        try(Connection c = source.getConnection())
        {
           List<String> tables = new ArrayList<String>();
           
            try(ResultSet rs = c.getMetaData().getTables(null, null, "%", null))
            {
                while(rs.next()) {
                   tables.add(rs.getString(3));
                }
            }
            try(Statement s = c.createStatement()){
                for(String t : tables) {
                    s.execute("drop table "+t);
                }
            }
            ClassLoader cl = QueryExecutor.class.getClassLoader();
            
            new QueryExecutor().execute(c,cl.getResourceAsStream("createDB.sql"),pw);
           
            try(ResultSet rs = c.getMetaData().getTables(null, null, "%", null))
            {
                while(rs.next()) {
                   pw.println(rs.getString(3)+"<br/>");
                }
            }
            
        } catch(SQLException | IOException  e ) {
            
            e.printStackTrace(pw);
            
        }
        
        
        return sw.toString();
    }
    
}
