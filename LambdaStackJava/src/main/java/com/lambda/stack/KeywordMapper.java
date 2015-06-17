/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lambda.stack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.spark.api.java.function.FlatMapFunction;
import scala.Tuple2;
import twitter4j.Status;

/**
 *
 * @author Dariusz Hudziak
 */
public class KeywordMapper implements FlatMapFunction<Status, Tuple2<Status,String>> {
    @Override
    public Iterable<Tuple2<Status, String>> call(Status t) throws Exception {
       List<Object[]> keywords = null;
       DatabaseHelper dh = null;
       
       try {
           dh = DatabaseHelper.openDB();
           keywords = dh.listKeywords();
       } catch(SQLException e) {
           e.printStackTrace();
       } finally {
           if(dh!=null) dh.close();
       }
       
       Iterator<Object[]> keyIter = keywords.iterator();
       while(keyIter.hasNext()) {
           Object[] o = keyIter.next();
           if(!t.getText().contains((String)o[0])){
               keyIter.remove();
           }
       }
       
       List<Tuple2<Status, String>> list = new ArrayList<>();
       
       for(Object[] o : keywords) {
           list.add(new Tuple2<>(t,(String)o[0]));
       }
       
       return list;
    }   
}
