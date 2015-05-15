/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.agh.iosr.lsf.dao;

import java.util.List;
import pl.edu.agh.iosr.lsf.model.Tweet;

/**
 *
 * @author uriel
 */
public interface TweetDAO {
    
    int addTweet(Tweet t);
    void updateTweet(Tweet t);
    List<Tweet> listTweets();
    Tweet getTweet(int id);
    void removeTweet(int id);
    
}
