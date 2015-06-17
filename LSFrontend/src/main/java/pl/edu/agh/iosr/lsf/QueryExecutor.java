/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.agh.iosr.lsf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author Dariusz Hudziak
 */
@Service
public class QueryExecutor {
    
   
   public List<String> select(String sql) throws SQLException {
       return null;
   } 
    
   public void execute(Connection dbc,InputStream in,PrintWriter pw) throws SQLException,IOException {
       BufferedReader br = new BufferedReader(new InputStreamReader(in,Charset.forName("UTF-8")));
       String line;
       StringBuffer sql = new StringBuffer();
       
       try(Statement s = dbc.createStatement())
       {
           
        while(true) {
          line = br.readLine();
          if(line==null) break;
          line = line.trim();
          int i = line.indexOf("--");
          if(i>=0) {
            line = line.substring(0,i).trim();
          }
          sql.append(line);
          if(line.endsWith(";")) {
              //pw.println("execute: ("+sql.toString()+");");
              s.execute(sql.toString());
              sql.delete(0, sql.length());
          }
        }
        if(sql.length() > 0){
            s.execute(sql.toString());
        }
       }
       
   }
}
