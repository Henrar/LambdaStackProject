/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lambda.stack;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import scala.Tuple2;
import scala.Tuple3;

/**
 *
 * @author Dariusz Hudziak
 */
public class HashTagUsageWritter implements Function<JavaPairRDD<Tuple3<Integer, Integer,Integer>, Integer>, Void> 
{
    @Override
    public Void call(JavaPairRDD<Tuple3<Integer, Integer,Integer>, Integer> t1)
    throws Exception
    {
        Map<Tuple3<Integer,Integer,Integer>,Integer> data = t1.collectAsMap();
        Map<Tuple2<Integer,Integer>,Integer>  cat2cat = new HashMap<>();
        
        DatabaseHelper dh = null;
        
        try{
            dh = DatabaseHelper.openDB();
            for( Tuple3<Integer,Integer,Integer> key : data.keySet() ) {
                
                Integer ucat = cat2cat.get(new Tuple2(key._1(),key._2()));
                if(ucat==null){
                    ucat = dh.insertTagCategoryUsage(
                            new Date(System.currentTimeMillis()), 
                            key._2(), 
                            key._1());
                    cat2cat.put(new Tuple2(key._1(),key._2()), ucat);
                }
                dh.insertTagKeywordUsage(ucat, key._3(), (Integer)data.get(key));   
            }
            System.out.println("Save Tags Count: "+data.keySet().size());
        }catch(SQLException e) {
            e.printStackTrace();
        }finally {
            if(dh!=null)dh.close();
        }
           
        
        return null;
    }
 
}
