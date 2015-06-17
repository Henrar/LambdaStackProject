package com.lambda.stack;

import java.sql.SQLException;
import org.apache.log4j.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
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
                   for(Object[] s : dh.listKeywords()){
                       
                       if(txt.contains(s[0].toString())){
                        l.add(new Tuple2<>(s[0].toString(),Integer.valueOf(1)));
                       }
                   }
                for( HashtagEntity he : t.getHashtagEntities()) {
                    String text = he.getText(); 
                    if(!text.matches("[a-zA-Z0-9]*")) continue;
                    
                    int id = dh.findHashTag(text);
                    if(id<0) {
                       id = dh.insertHashTag(text);
                    }
                    
                   // l.add(new Tuple2<>(text.toUpperCase(),Integer.valueOf(1)));
                }
                dh.close();
               }catch(SQLException e ) {
                   e.printStackTrace();
               }
               
               
//              
               return l;
            }
        }
        );
    
        
//        tweets.flatMapToPair(new PairFlatMapFunction<Status, Integer, Integer>() {
//            @Override
//            public Iterable<Tuple2<Integer, Integer>> call(Status t) throws Exception {
//               
//                List<Tuple2<Integer,Integer>> result = new LinkedList<>();
//                
//                int tag_id = -1;
//                try{
//                    DatabaseHelper dh = DatabaseHelper.openDB();
//                    
//                    for(HashtagEntity he : t.getHashtagEntities()) {
//                        if(he.getText().matches("[a-zA-Z0-9]*")){
//                            tag_id = dh.findHashTag(he.getText());
//                            if(tag_id <= 0)
//                                tag_id = dh.insertHashTag(he.getText());
//                        }
//                        result.add(new Tuple2<>(tag_id,Integer.valueOf(1)));
//                    }
//                    
//                    dh.close();
//                }catch(SQLException e) {
//                    e.printStackTrace();
//                }
//                
//                return result;
//            }
//        }).reduceByKey(new Function2<Integer, Integer, Integer>() {
//            @Override
//            public Integer call(Integer t1, Integer t2) throws Exception {
//                return t1+t2;
//            }
//        }).foreach(new Function<JavaPairRDD<Integer, Integer>, Void>() {
//            @Override
//            public Void call(JavaPairRDD<Integer, Integer> t1) throws Exception {
//               Map<Integer,Integer> val = t1.collectAsMap();
//               Date cd = new Date(System.currentTimeMillis());
//               
//               try{
//                   DatabaseHelper dh = DatabaseHelper.openDB();
//                   for(Integer tag : val.keySet()){
//                       dh.insertTagActivity(tag, cd, val.get(tag));
//                   }
//                   dh.close();
//                   
//               }catch(SQLException e){
//                    e.printStackTrace();
//               }
//               return null;
//            }
//        });
//        
//        tweets.flatMapToPair(new PairFlatMapFunction<Status, Integer, Integer>() {
//            @Override
//            public Iterable<Tuple2<Integer, Integer>> call(Status t) throws Exception {
//                String name = t.getUser().getName();
//                
//                if(!name.matches("[a-zA-Z0-9]*")){
//                    return Collections.emptyList();
//                }
//                
//                String id = Long.toString(t.getUser().getId());
//                int user_id = -1;
//                try{
//                    DatabaseHelper dh = DatabaseHelper.openDB();
//                    
//                    user_id = dh.findUser(id);
//                    if(user_id <= 0) {
//                        user_id = dh.insertUser(id, name);
//                    }
//                    
//                    dh.close();
//                }catch(SQLException e) {
//                    e.printStackTrace();
//                }
//                
//                return Collections.singleton(new Tuple2<Integer,Integer>(user_id,Integer.valueOf(1)));
//            }
//        }).reduceByKey(new Function2<Integer, Integer, Integer>() {
//            @Override
//            public Integer call(Integer t1, Integer t2) throws Exception {
//                return t1+t2;
//            }
//        }).foreach(new Function<JavaPairRDD<Integer, Integer>, Void>() {
//            @Override
//            public Void call(JavaPairRDD<Integer, Integer> t1) throws Exception {
//               Map<Integer,Integer> val = t1.collectAsMap();
//               Date cd = new Date(System.currentTimeMillis());
//               
//               try{
//                   DatabaseHelper dh = DatabaseHelper.openDB();
//                   for(Integer user : val.keySet()){
//                       dh.insertUserActivity(user, cd, val.get(user));
//                   }
//                   dh.close();
//                   
//               }catch(SQLException e){
//                    e.printStackTrace();
//               }
//               return null;
//            }
//        });
//        

        tweets.flatMap(new KeywordMapper())
              .flatMapToPair(new PairFlatMapFunction<Tuple2<Status, String>, String, Integer>() {

            @Override
            public Iterable<Tuple2<String, Integer>> call(Tuple2<Status, String> t) throws Exception {
                return Collections.singletonList(new Tuple2<>(t._2,Integer.valueOf(1)));
            }
        }).reduceByKey(new Function2<Integer, Integer, Integer>(){
            @Override
            public Integer call(Integer t1, Integer t2) throws Exception {
                return t1+t2;
            } 
        }).print();
        
        
        
        
//        JavaPairDStream<String,Integer> tagsc = tags.reduceByKey(new Function2<Integer, Integer, Integer>() {
//
//            @Override
//            public Integer call(Integer t1, Integer t2) throws Exception {
//                return t1 + t2;
//            }
//        });
//      
//        tagsc.foreach(new Function<JavaPairRDD<String, Integer>, Void>() {
//            @Override
//            public Void call(JavaPairRDD<String, Integer> t1) throws Exception {
//               Map<String,Integer> m = t1.collectAsMap();
//               
//               DatabaseHelper dh = DatabaseHelper.openDB();
//               for(String s : m.keySet()) {
//                   
//                   int id = dh.findKeyword(s);
//                   dh.insertKeywordUsage(id, m.get(s).intValue());
//                   System.out.println("s: "+s+" "+m.get(s));
//               }
//               dh.close();
//                System.out.println("==== MARK ==== ");
//               return null;
//            }
//        });
       
        ssc.start();
        ssc.awaitTermination();
    }
}
