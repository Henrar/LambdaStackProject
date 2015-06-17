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
import scala.Tuple3;
import twitter4j.Status;

/**
 *
 * @author Dariusz Hudziak
 */
public class KeywordMapper implements FlatMapFunction<Status, Tuple3<Status,Integer,Integer>> {
    @Override
    public Iterable<Tuple3<Status, Integer ,Integer>> call(Status t) throws Exception {
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
       
       List<Tuple3<Status, Integer,Integer>> list = new ArrayList<>();
       
       for(Object[] o : keywords) {
           list.add(new Tuple3<>(t,(Integer)o[1],(Integer)o[2]));
       }
       
       return list;
    }   
}
