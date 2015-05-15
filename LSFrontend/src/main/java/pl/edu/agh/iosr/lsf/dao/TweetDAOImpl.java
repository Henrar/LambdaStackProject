/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.agh.iosr.lsf.dao;

import java.nio.channels.SeekableByteChannel;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import pl.edu.agh.iosr.lsf.model.Tweet;

/**
 *
 * @author uriel
 */

@Repository
public class TweetDAOImpl implements TweetDAO {

    SessionFactory sf;
    
    public void setSessionFactory(SessionFactory sf){
        this.sf = sf;
    }
    
    @Override
    public int addTweet(Tweet t) {
       sf.getCurrentSession().persist(t);
       return t.getId();
    }

    @Override
    public void updateTweet(Tweet t) {
       sf.getCurrentSession().update(t);
    }

    @Override
    public List<Tweet> listTweets() {
        return sf.getCurrentSession().createCriteria(Tweet.class).list();
    }

    @Override
    public Tweet getTweet(int id) {
       List l = sf.getCurrentSession().createCriteria(Tweet.class).add(Restrictions.eq("id", id)).list();
       if(l.size() < 1 ) {
           return null;
       } 
       return (Tweet) l.get(0);
    }

    @Override
    public void removeTweet(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
