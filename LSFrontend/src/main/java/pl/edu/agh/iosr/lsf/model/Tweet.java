/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.agh.iosr.lsf.model;

import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author uriel
 */
@Entity
@Table(name = "TBL_TWEET")
public class Tweet {
    
    @Id
    @Column(name="FLD__ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    
    @Column(name="FLD_DATE")
    private Date date;
    
    @Column(name="FLD_CONTENT")
    private String content;

    public Tweet() {
    }

    
    
    public Tweet(Date date, String content) {
        this.date = date;
        this.content = content;
    }

    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return date.toString()+" "+content;
    }
    
    
    
    
}
