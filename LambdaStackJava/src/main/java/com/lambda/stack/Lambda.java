package com.lambda.stack;

import java.sql.SQLException;
import org.apache.log4j.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
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

        JavaStreamingContext ssc = new JavaStreamingContext(sparkUrl, "Twitter", new Duration(batch*1000), sparkHome, new String[]{jarFile});

        JavaDStream<Status> tweets = TwitterUtils.createStream(ssc).filter(new Function<Status, Boolean>() {
            @Override
            public Boolean call(Status t1) throws Exception {
                return "en".equals(t1.getUser().getLang());
            }
        }
        );
        
//        tweets.flatMap(new FlatMapFunction<Status, Status>() {
//            @Override
//            public Iterable<Status> call(Status t) throws Exception {
//                
//            }
//        });
         
        // Tutaj się dane agregują buduje się zbiór np z ostniej minuty - i potem 
        // przesuwamy się nad nim co sekunde i przetwarzamy te same dane są przetwarzane
        // po kilka razy, ale za to jest ich wiecej
         tweets = tweets.window(new Duration(window*1000), 
                                new Duration(slide*1000) );
        
        
        JavaPairDStream<String,Integer> tags= tweets.flatMapToPair(new PairFlatMapFunction<Status, String, Integer>() 
        {
            @Override
            public Iterable<Tuple2<String, Integer>> call(Status t) throws Exception {
               List<Tuple2<String,Integer>> l = new ArrayList<>(t.getHashtagEntities().length);
               
               try {
                   DatabaseHelper dh = DatabaseHelper.openDB();
                   String txt = t.getText();
                   for(String s : dh.listKeywords()){
                       
                       if(txt.contains(s)){
                        l.add(new Tuple2<>(s,Integer.valueOf(1)));
                       }
                   }
//                for( HashtagEntity he : t.getHashtagEntities()) {
//                    String text = he.getText(); 
//                    if(!text.matches("[a-zA-Z0-9]*")) continue;
//                    
//                    int id = dh.findHashTag(text);
//                    if(id<0) {
//                       id = dh.insertHashTag(text);
//                    }
//                    
//                    l.add(new Tuple2<>(text.toUpperCase(),Integer.valueOf(1)));
//                }
                dh.close();
               }catch(SQLException e ) {
                   e.printStackTrace();
               }
               
               
//               if(count){
//               String s = t.getText();
//               if(s.contains("like")) {
//                   l.add(new Tuple2<>("like",Integer.valueOf(1)));
//               }
//               if(s.contains("love")) {
//                   l.add(new Tuple2<>("love",Integer.valueOf(1)));
//               }
//               if(s.contains("hate")) {
//                   l.add(new Tuple2<>("hate",Integer.valueOf(1)));
//               }
//               if(s.contains("take")) {
//                   l.add(new Tuple2<>("take",Integer.valueOf(1)));
//               }
//                if(s.contains("admire")) {
//                   l.add(new Tuple2<>("admire",Integer.valueOf(1)));
//               }
//               }
               return l;
            }
        }
        );
        
        JavaPairDStream<String,String> tagtext = tweets.flatMapToPair(new PairFlatMapFunction<Status, String, String>() {
            @Override
            public Iterable<Tuple2<String, String>> call(Status t) throws Exception {
               List<Tuple2<String,String>> l = new ArrayList<>(t.getHashtagEntities().length);
               
               for( HashtagEntity he : t.getHashtagEntities()) {
                   l.add(new Tuple2<>(he.getText(),t.getText()));
               }
               
               return l;
            }
        }).reduceByKey(new Function2<String, String, String>() {
            @Override
            public String call(String t1, String t2) throws Exception {
                return t1+" "+t2;
            }
        });
        
        
        JavaPairDStream<String,Integer> tagsc = tags.reduceByKey(new Function2<Integer, Integer, Integer>() {

            @Override
            public Integer call(Integer t1, Integer t2) throws Exception {
                return t1 + t2;
            }
        });
      
        tagsc.foreach(new Function<JavaPairRDD<String, Integer>, Void>() {
            @Override
            public Void call(JavaPairRDD<String, Integer> t1) throws Exception {
               Map<String,Integer> m = t1.collectAsMap();
               for(String s : m.keySet()) {
                   System.out.println("s: "+s+" "+m.get(s));
               }
                System.out.println("==== MARK ==== ");
               return null;
            }
        });
       
        ssc.start();
        ssc.awaitTermination();
    }
}
