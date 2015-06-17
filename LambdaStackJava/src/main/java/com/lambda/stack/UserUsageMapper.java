/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lambda.stack;

import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;
import scala.Tuple3;
import twitter4j.Status;

/**
 *
 * @author uriel
 */
public class UserUsageMapper implements PairFlatMapFunction<Tuple3<Status,Integer,Integer>, Tuple3<Integer,Integer,Integer>, Integer> {

    @Override
    public Iterable<Tuple2<Tuple3<Integer, Integer, Integer>, Integer>> call(Tuple3<Status, Integer, Integer> t) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
