/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lambda.stack;

import java.util.LinkedList;
import java.util.List;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;
import twitter4j.HashtagEntity;
import twitter4j.Status;

/**
 *
 * @author uriel
 */
public class HashTagMapper implements PairFlatMapFunction<Status, String, Integer> {

    @Override
    public Iterable<Tuple2<String, Integer>> call(Status t) throws Exception {
        List<Tuple2<String,Integer>> result = new LinkedList<>();
        
        for(HashtagEntity he : t.getHashtagEntities()) {
            result.add(new Tuple2<>(he.getText(),Integer.valueOf(1)));
                   
        }
        
        return result;
    }
    
}
