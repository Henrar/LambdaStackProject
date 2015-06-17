/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lambda.stack;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;

/**
 *
 * @author uriel
 */
public class HashTagWriter implements Function<JavaPairRDD<String, Integer>, Void>  {
    @Override
    public Void call(JavaPairRDD<String, Integer> t1) throws Exception {
        
        Map<String,Integer> data = t1.collectAsMap();
        DatabaseHelper dh = null;
        
        try{
            dh = DatabaseHelper.openDB();
            for(String tag : data.keySet()){
                int tagID = dh.findHashTag(tag);
                if(tagID <= 0 ){
                    tagID = dh.insertHashTag(tag);
                }
                dh.insertTagActivity(tagID, new Date(System.currentTimeMillis()), data.get(tag));
            }
        }catch(SQLException e){
            e.printStackTrace();
        } finally {
            if(dh!=null) dh.close();
        }
        
        return null;
    }
    
}
