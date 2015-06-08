package com.lambda.stack;

import org.apache.log4j.Level;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import scala.Tuple2;

/**
 * @author Henrar
 * @version 0.2
 */
public final class Lambda {

    private static TwitterHelper twitterHelper;
    private static DatabaseHelper databaseHelper;

    public static void main(String[] args) throws Exception {
        String sparkHome = "../../opt/spark";
        String sparkUrl = "local[4]";
        String jarFile = "/home/ubuntu/jst.jar";

        twitterHelper = new TwitterHelper();
        databaseHelper = new DatabaseHelper();
        twitterHelper.configureTwitterCredentials();
        databaseHelper.connectToDatabase();
        WordProcessing.init();


        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);

        JavaStreamingContext ssc = new JavaStreamingContext(sparkUrl, "Twitter", new Duration(1000), sparkHome, new String[]{jarFile});

        JavaDStream<Status> tweets = TwitterUtils.createStream(ssc).filter(new Function<Status, Boolean>() {
            @Override
            public Boolean call(Status t1) throws Exception {
                return "en".equals(t1.getUser().getLang());
            }
        }
        );
       
        JavaPairDStream<String,Integer> tags = tweets.flatMapToPair(new PairFlatMapFunction<Status, String, Integer>() {
            @Override
            public Iterable<Tuple2<String, Integer>> call(Status t) throws Exception {
               List<Tuple2<String,Integer>> l = new ArrayList<>(t.getHashtagEntities().length);
               
               for( HashtagEntity he : t.getHashtagEntities()) {
                   l.add(new Tuple2<>(he.getText(),Integer.valueOf(1)));
               }
               
               return l;
            }
        }
        );
        JavaPairDStream<String,Integer> tagsc = tags.reduceByKey(new Function2<Integer, Integer, Integer>() {

            @Override
            public Integer call(Integer t1, Integer t2) throws Exception {
                return t1 + t2;
            }
        });
      
       tagsc.print();
       
        ssc.start();
        ssc.awaitTermination();
    }
}
