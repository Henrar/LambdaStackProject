package com.lambda.stack;

import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.*;

import java.util.List;
import java.util.Properties;

/**
 * Created by Henrar on 2015-05-29.
 */
public class WordProcessing {
    public void processWords(String [] inputWords) {
        String text = "I am feeling very upset";
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, sentiment");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = pipeline.process(text);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            String sentiment = sentence.get(SentimentCoreAnnotations.ClassName.class);
            System.out.println(sentiment + "\t" + sentence);
        }
    }
}
