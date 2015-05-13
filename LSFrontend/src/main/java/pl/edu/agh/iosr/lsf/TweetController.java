/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.agh.iosr.lsf;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author uriel
 */
@RestController
@RequestMapping("/tweets")
public class TweetController {
    
    @RequestMapping(method = RequestMethod.GET)
    public String tweet() {
        return "Hello REST";
    }
    
    
}
