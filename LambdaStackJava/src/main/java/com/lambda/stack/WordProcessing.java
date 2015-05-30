package com.lambda.stack;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.apache.spark.streaming.api.java.JavaDStream;

import java.util.Properties;

/**
 * Created by Henrar on 2015-05-29.
 */

public class WordProcessing {
    static StanfordCoreNLP pipeline;

    public static void init() {
        Properties props = new Properties();
        props.put(
                "annotators",
                "tokenize, ssplit, pos, lemma, ner, parse");

        pipeline = new StanfordCoreNLP(props);
    }

    public static void findSentimentForSingleTweet(String tweet) {

        int mainSentiment = 0;
        if (tweet != null && tweet.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(tweet);
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String partText = sentence.toString();
                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }
            }
        }
        System.out.println("DEBUG: " + mainSentiment);
    }

    public static void findSentimentForTweetStream(JavaDStream<String> statuses) {
        int [] sentiments;
    }
}