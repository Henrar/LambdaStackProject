/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.agh.iosr.lsf;

import com.sun.java.swing.plaf.windows.WindowsTreeUI;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.iosr.lsf.dao.TweetDAO;
import pl.edu.agh.iosr.lsf.model.Tweet;
import pl.edu.agh.iosr.lsf.srv.TweetService;

/**
 *
 * @author uriel
 */
@RestController
@RequestMapping("/tweets")
public class TweetController {
    
    private TweetService tweet;
    
    @Autowired
    @Qualifier(value="personService")
    public void setTweetService(TweetService dao) {
        tweet = dao;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public List<Tweet> getTweets() {
        return tweet.listTweets();
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Tweet getTweets(@PathVariable() int id) {
         return tweet.getTweet(id);
    }
   
    @RequestMapping(method = RequestMethod.POST )
    public Tweet saveTweet(@RequestPart(value = "date") String date,
                            @RequestPart(value = "name") String name)
    {
        try {
            tweet.addTweet(new Tweet(new Date(new SimpleDateFormat("yyyy-mm-dd").parse(date).getTime()),name));
        } catch (ParseException ex) {
            Logger.getLogger(TweetController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
}
