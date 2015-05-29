package com.lambda.stack;

import org.apache.spark.api.java.function.*;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.twitter.*;
import org.apache.spark.streaming.api.java.*;
import twitter4j.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

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


        JavaDStream<Status> tweets = TwitterUtils.createStream(ssc);
        WordProcessing.findSentimentForSingleTweet(tweets.toString());
        JavaDStream<String> statuses = tweets.map(new Function<Status, String>() {
            public String call(Status status) { return status.getText(); }
        }
        );

        //statuses.print();
        ssc.start();
        ssc.awaitTermination();
    }
}
