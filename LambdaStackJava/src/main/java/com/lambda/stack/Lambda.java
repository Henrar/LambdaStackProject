package com.lambda.stack;

import org.apache.spark.api.java.function.*;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.twitter.*;
import org.apache.spark.streaming.api.java.*;
import twitter4j.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Henrar
 * @version 0.1
 */
public final class Lambda {
    public static void configureTwitterCredentials() throws Exception {
        File file = new File("/home/ubuntu/Twitter/twitter.txt");
        if (!file.exists()) {
            try {
                throw new Exception("Could not find configuration file " + file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<String> lines = readLines(file);
        HashMap<String, String> map = new HashMap<>();
        for (String line : lines) {
            String[] splits = line.split("=");
            if (splits.length != 2) {
                try {
                    throw new Exception("Error parsing configuration file - incorrectly formatted line [" + line + "]");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            map.put(splits[0].trim(), splits[1].trim());
        }
        String[] configKeys = {"consumerKey", "consumerSecret", "accessToken", "accessTokenSecret"};
        for (String key : configKeys) {
            String value = map.get(key);
            if (value == null) {
                try {
                    throw new Exception("Error setting OAuth authentication - value for " + key + " not found");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (value.length() == 0) {
                try {
                    throw new Exception("Error setting OAuth authentication - value for " + key + " is empty");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String fullKey = "twitter4j.oauth." + key;
            System.setProperty(fullKey, value);
            System.out.println("\tProperty " + fullKey + " set as " + value);
        }
        System.out.println();
    }
    private static List<String> readLines(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.length() > 0) lines.add(line);
        }
        bufferedReader.close();
        return lines;
    }

    public static void main(String[] args) throws Exception {
        String sparkHome = "../../opt/spark";
        String sparkUrl = "local[4]";
        String jarFile = "/home/ubuntu/jst.jar";
        Lambda.configureTwitterCredentials();

        JavaStreamingContext ssc = new JavaStreamingContext(sparkUrl, "Twitter", new Duration(1000), sparkHome, new String[]{jarFile});

        JavaDStream<Status> tweets = TwitterUtils.createStream(ssc);
        JavaDStream<String> statuses = tweets.map(new Function<Status, String>() {
            public String call(Status status) { return status.getText(); }
        }
        );
        statuses.print();
        ssc.start();
        ssc.awaitTermination();
    }
}
