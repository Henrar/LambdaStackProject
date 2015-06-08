package com.lambda.stack;

import org.apache.log4j.Level;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import scala.Tuple2;
import twitter4j.HashtagEntity;
import twitter4j.Status;

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

        long batch = 1;
        long window = 10;
        long slide=1;
        
        if( args.length > 0 ) {
            batch = Long.parseLong(args[0]);
        }
        
        if( args.length > 1 ) {
            window = Long.parseLong(args[1]);
        }
        
        if( args.length > 2 ) {
            slide = Long.parseLong(args[2]);
        }
        
        twitterHelper = new TwitterHelper();
        databaseHelper = new DatabaseHelper();
        twitterHelper.configureTwitterCredentials();
        databaseHelper.connectToDatabase();
       
        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);

        JavaStreamingContext ssc = new JavaStreamingContext(sparkUrl, "Twitter", new Duration(batch*1000), sparkHome, new String[]{jarFile});

        JavaDStream<Status> tweets = TwitterUtils.createStream(ssc).filter(new Function<Status, Boolean>() {
            @Override
            public Boolean call(Status t1) throws Exception {
                return "en".equals(t1.getUser().getLang());
            }
        }
        );
        
        
        
        JavaPairDStream<String,Integer> tags = tweets.window(new Duration(window*1000), 
                                                             new Duration(slide*1000) )
                                                     .flatMapToPair(new PairFlatMapFunction<Status, String, Integer>() 
        {
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
      
       
        ssc.start();
        ssc.awaitTermination();
    }
}
