/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lambda.stack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;
import scala.Tuple3;
import twitter4j.HashtagEntity;
import twitter4j.Status;

/**
 *
 * @author Dariusz Hudziak
 */
public class HashTagUsageMapper implements PairFlatMapFunction<Tuple3<Status,Integer,Integer>, Tuple3<Integer,Integer,Integer>, Integer> {

    @Override
    public Iterable<Tuple2<Tuple3<Integer, Integer,Integer>, Integer>> call(Tuple3<Status, Integer,Integer> t) throws Exception {
       List<Tuple2<Tuple3<Integer,Integer,Integer>,Integer>> result = new ArrayList<>();
       Set<String> tags = new HashSet<>();
       
        for(HashtagEntity he : t._1().getHashtagEntities()) {
            tags.add(he.getText());
        }
       
       DatabaseHelper dh = null;
       
       try{
           dh = DatabaseHelper.openDB();
           for(Object[] o : dh.listMonitoredTags() ) {
               //if(tags.contains(o[0])){
                   result.add(new Tuple2(new Tuple3(o[1],t._2(),t._3()),
                              Integer.valueOf(1)));
               //}
           }
       }catch(SQLException e){
           if(dh!=null) dh.close();
       }
       
       
       return result;
    }
}
