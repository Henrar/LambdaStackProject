/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lambda.stack;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.spark.api.java.function.FlatMapFunction;
import scala.Tuple3;
import twitter4j.HashtagEntity;
import twitter4j.Status;

/**
 *
 * @author uriel
 */
public class HashTagMapper implements FlatMapFunction<Status, Tuple3<String,String,Integer>> {

    @Override
    public Iterable<Tuple3<String, String, Integer>> call(Status t) throws Exception {
        List<Tuple3<String,String,Integer>> result = new LinkedList<>();
        List<HashtagEntity> htl = Arrays.asList(t.getHashtagEntities());
        
        for(HashtagEntity he : htl ) {
            result.add(new Tuple3<String,String,Integer>(he.getText(),null,Integer.valueOf(1)));
        }
        
        return result;
    }
    
}
