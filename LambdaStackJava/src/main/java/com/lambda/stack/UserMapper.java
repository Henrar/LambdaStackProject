/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lambda.stack;

import java.util.Collections;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;
import twitter4j.Status;

/**
 *
 * @author uriel
 */
public class UserMapper implements PairFlatMapFunction<Status, String, Integer> {
    @Override
    public Iterable<Tuple2<String, Integer>> call(Status t) throws Exception {
        return Collections.singletonList(new Tuple2<>(t.getUser().getName(),Integer.valueOf(1)));
    }
    
}
