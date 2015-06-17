/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lambda.stack;

import org.apache.spark.api.java.function.Function2;

/**
 *
 * @author Dariusz Hudziak
 */
public class Sumator implements Function2<Integer, Integer, Integer> {
    @Override
    public Integer call(Integer t1, Integer t2) throws Exception {
       return t1+t2;
    }
}
