package com.lambda.stack;

import java.sql.SQLException;
import org.apache.log4j.Level;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import scala.Tuple2;
import scala.Tuple3;
import twitter4j.HashtagEntity;
import twitter4j.Status;

/**
 * @author Henrar
 * @version 0.2
 */
public final class Lambda {

    private static TwitterHelper twitterHelper;


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
        DatabaseHelper.init();
        twitterHelper.configureTwitterCredentials();
//        databaseHelper.connectToDatabase();
       
        
        
        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);

        JavaStreamingContext ssc = new JavaStreamingContext(sparkUrl, "Twitter", new Duration(batch*1000));

        JavaDStream<Status> tweets = TwitterUtils.createStream(ssc).filter(new Function<Status, Boolean>() {
            @Override
            public Boolean call(Status t1) throws Exception {
                return "en".equals(t1.getUser().getLang());
            }
        }
        );
        
        // Tutaj się dane agregują buduje się zbiór np z ostniej minuty - i potem 
        // przesuwamy się nad nim co sekunde i przetwarzamy te same dane są przetwarzane
        // po kilka razy, ale za to jest ich wiecej
         tweets = tweets.window(new Duration(window*1000), 
                                new Duration(slide*1000) );
        
        
        // Rejestruj aktwyność hashtagów
        tweets.flatMapToPair(new HashTagMapper())
              .reduceByKey(new Sumator())
              .foreach(new HashTagWriter());
        
        // Rejestrj aktywność uzytkownika
        tweets.flatMapToPair(new UserMapper())
              .reduceByKey(new Sumator())
              .foreach(new UserWritter());
        
        JavaDStream<Tuple3<Status,Integer,Integer>> keywordStream = tweets.flatMap(new KeywordMapper());
        
        keywordStream.flatMapToPair(new HashTagUsageMapper())
        .reduceByKey(new Sumator())
        .foreach(new HashTagUsageWritter());
             
       
        ssc.start();
        ssc.awaitTermination();
    }
}
