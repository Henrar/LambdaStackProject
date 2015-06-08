package pl.edu.agh.iosr.lsf.srv;


import java.util.List;
import pl.edu.agh.iosr.lsf.model.Keyword;
import pl.edu.agh.iosr.lsf.model.Tweet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author uriel
 */
public interface TweetService {
    
    int addTweet(Tweet t);
    void updateTweet(Tweet t);
    List<Tweet> listTweets();
    Tweet getTweet(int id);
    void removeTweet(int id);
    
    void addKeyword(Keyword k);
    void addKeyword(String name,String category);
    List<Keyword> listKeywords();
}
