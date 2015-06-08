/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.agh.iosr.lsf.srv;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.iosr.lsf.dao.KeywordDAO;
import pl.edu.agh.iosr.lsf.dao.TweetDAO;
import pl.edu.agh.iosr.lsf.model.Keyword;
import pl.edu.agh.iosr.lsf.model.Tweet;

/**
 *
 * @author uriel
 */
@Service
public class TweetServiceImpl implements TweetService {

    // DB 79
    
    private TweetDAO dao;
    private KeywordDAO kdao;
    
    @Qualifier(value="keywordDAO")
    public void setKeywordDAO(KeywordDAO d){
        kdao = d;
    }
    
    @Qualifier(value="personDAO")
    public void setTweetDAO(TweetDAO dao) {
        this.dao = dao;
    }
    
    
    @Override
    @Transactional
    public int addTweet(Tweet t) {
        return dao.addTweet(t);
    }

    @Override
    @Transactional
    public void updateTweet(Tweet t) {
        dao.updateTweet(t);
    }

    @Override
    @Transactional
    public List<Tweet> listTweets() {
        return dao.listTweets();
    }

    @Override
    @Transactional
    public Tweet getTweet(int id) {
       return dao.getTweet(id);
    }

    @Override
    @Transactional
    public void removeTweet(int id) {
        dao.removeTweet(id);
    }

    @Override
    @Transactional
    public void addKeyword(Keyword k) {
        kdao.addKeyword(k);
    }

    @Override
     @Transactional
    public void addKeyword(String name, String category) {
        kdao.addKeyword(name, category);
    }
    
    

    @Override
    @Transactional
    public List<Keyword> listKeywords() {
        return kdao.listKeywords();
    }
    
    
    
}
