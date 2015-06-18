/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.agh.iosr.lsf;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.iosr.lsf.dao.DatabaseHelper;

/**
 *
 * @author Dariusz Hudziak
 */
@RestController
public class RootController {
    private DatabaseHelper dh;
    
    
    @Autowired
    public void setSource(DatabaseHelper ds) {
        dh = ds;
    }
    
    @RequestMapping(value="/",method = RequestMethod.GET)
    public String hello(){
        return "HELLO";
    }
    
    @RequestMapping(value = "/generate",method = RequestMethod.GET)
    public String regenerateDB() {
       return dh.regenerateDB();
    }
    
    @RequestMapping(value="/query",method = RequestMethod.POST)
    public List<String[]> executeQuery(@RequestPart(value = "query") String sql) {
        
        try
        {
           return dh.select(sql);
        }catch(SQLException e) {
            return Collections.singletonList(new String[]{e.getMessage()});
        }
        
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="Keyword">
    @RequestMapping(value="keyword",method = RequestMethod.GET)
    public List<String[]> listKeywords(){
        try {
            return dh.listKeywords();
        } catch(SQLException e) {
            return Collections.singletonList(new String[]{e.getMessage()});
        }
    }
    @RequestMapping(value="keyword",method = RequestMethod.PUT)
    public String insertKeyword(@RequestParam(value = "name") String key,
            @RequestParam(value = "category") String cat){
        try {
            return ""+dh.insertKeyword(key, cat);
        } catch(SQLException e) {
            return e.getMessage();
        }
    }
    @RequestMapping(value="keyword",method = RequestMethod.DELETE)
    public String deleteKeyword(@RequestParam(value = "name") String key){
        try {
            dh.deleteKeyword(key);
            return "OK";
        } catch(SQLException e) {
            return e.getMessage();
        }
    }
   
    
    @RequestMapping(value="keyword/gen",method = RequestMethod.GET)
    public String genKeywords(@RequestParam(value = "n") int n) {
        
         int c =0;
        try{
            for( String[] s : dh.listKeywords()) {
                dh.deleteKeyword(s[1]);
            }
           
            for( String s : DatabaseHelper.loadWords().subList(0, n)) {
                dh.insertKeyword(s, "positive");
                c++;
            }
        }catch(SQLException e){
           return e.getMessage();
        }
        
        return "OK "+c;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Tag">
    @RequestMapping(value="/tag",method = RequestMethod.GET)
    public List<String[]> listTags()
    {
        try{
           
           return dh.listTags();
          
        }catch(SQLException e){
            return Collections.singletonList(new String[]{e.getMessage()});
        }
    }
    @RequestMapping(value = "tag/monitored",method = RequestMethod.GET)
    public List<String[]> listMonitoredTags()
    {
        try{
           
           return dh.listMonitoredTags();
          
        }catch(SQLException e){
            return Collections.singletonList(new String[]{e.getMessage()});
        }
    }
    @RequestMapping(value = "tag/monitored",method = RequestMethod.POST)
    public String monitorTag(@RequestParam(value = "name") String tag) throws SQLException 
    {
        try {
            dh.monitorTag(tag);
            return "OK";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }
    @RequestMapping(value = "tag/activity",method = RequestMethod.GET)
    public List<String[]> tagActivity(@RequestParam(value = "from",required = false,defaultValue = "") String from,
                                      @RequestParam(value = "to", required =  false,defaultValue = "") String to) throws SQLException 
    {
        try {
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Timestamp ft = from.length() < 1 ? new Timestamp(0):new Timestamp(sfd.parse(from).getTime());
            Timestamp tt = to.length() < 1 ? new Timestamp(System.currentTimeMillis()) : new Timestamp(sfd.parse(to).getTime());
            
            return dh.listTagsActivity(ft,tt);
            
        } catch (Exception e) {
            return Collections.singletonList(new String[]{e.getMessage()});
        }
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="User">
    @RequestMapping(value = "user",method = RequestMethod.GET)
    public List<String[]> listUsers()
    {
        try{
           
           return dh.listUsers();
          
        }catch(SQLException e){
            return Collections.singletonList(new String[]{e.getMessage()});
        }
    }
    @RequestMapping(value = "user/monitored",method = RequestMethod.GET)
    public List<String[]> listMonitoredUsers()
    {
        try{
           
           return dh.listMonitoredUsers();
          
        }catch(SQLException e){
            return Collections.singletonList(new String[]{e.getMessage()});
        }
    }
    @RequestMapping(value = "user/monitored",method = RequestMethod.POST)
    public String monitorUser(@RequestParam(value = "name") String tag) throws SQLException 
    {
        try {
            dh.monitorUser(tag);
            return "OK";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }
    @RequestMapping(value = "user/activity",method = RequestMethod.GET)
    public List<String[]> userActivity(@RequestParam(value = "from",required = false,defaultValue = "") String from,
                                       @RequestParam(value = "to", required =  false,defaultValue = "") String to) throws SQLException 
    {
        try {
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Timestamp ft = from.length() < 1 ? new Timestamp(0):new Timestamp(sfd.parse(from).getTime());
            Timestamp tt = to.length() < 1 ? new Timestamp(System.currentTimeMillis()) : new Timestamp(sfd.parse(to).getTime());
            
            return dh.listUsersActivity(ft,tt);
            
        } catch (Exception e) {
            return Collections.singletonList(new String[]{e.getMessage()});
        }
    }
//</editor-fold>
    
    
    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public List<String> listTests(){
        return DatabaseHelper.getTestStatemets();
    }
    
    @RequestMapping(value = "/test/{name}",method = RequestMethod.GET)
    public String runTest(@PathVariable(value = "name") String name) {
        try{
            return dh.benhmarkStatement(name)+"";
        }catch (SQLException e){
            return e.getMessage();
        }
    }
    @RequestMapping(value = "/test/{name}/select",method = RequestMethod.GET)
    public List<String[]> runTestselect(@PathVariable(value = "name") String name) {
        try{
            return dh.selectStatement(name);
        }catch (SQLException e){
           return Collections.singletonList(new String[]{e.getMessage()});
        }
    }
    @RequestMapping(value = "/test/{name}/show",method = RequestMethod.GET)
    public String runTestshow(@PathVariable(value = "name") String name) {
        return DatabaseHelper.getStatement(name);
    }
    
    @RequestMapping(value = "/delayTest",method = RequestMethod.GET)
    public String delayTest(@RequestParam(value = "keyword") String key,
                            @RequestParam(value = "category") String cat) {
        
        try {
        dh.deleteKeyword(key);
        dh.insertKeyword(key, cat);
        
        long start = System.currentTimeMillis();
        
       int c = 0; 
       while(!dh.tagKeywordUsage(key) && c < 60){
           try{
                Thread.sleep(1000);
                ++c;
           }catch(InterruptedException  e){
               
           }
       }
        return ""+(System.currentTimeMillis()-start);
        }catch(SQLException e){
           return e.getMessage();
        }
        
    }
    
}
