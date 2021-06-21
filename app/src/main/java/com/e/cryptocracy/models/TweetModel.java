package com.e.cryptocracy.models;

import com.e.cryptocracy.Util;

public class TweetModel {
    String date;
    String user_name;
    String user_image_link;
    String status;
    Boolean is_retweet;
    String status_link;
    String status_id;
    Integer retweet_count;
    Integer like_count;


    public String getDate() {
        return Util.parseDate(date, "h:mm a MMM d, ''yy", "yyyy-MM-dd'T'HH:mm:ssZ");
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_image_link() {
        return user_image_link;
    }

    public void setUser_image_link(String user_image_link) {
        this.user_image_link = user_image_link;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIs_retweet() {
        return is_retweet;
    }

    public void setIs_retweet(Boolean is_retweet) {
        this.is_retweet = is_retweet;
    }

    public String getStatus_link() {
        return status_link;
    }

    public void setStatus_link(String status_link) {
        this.status_link = status_link;
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }

    public Integer getRetweet_count() {
        return retweet_count;
    }

    public void setRetweet_count(Integer retweet_count) {
        this.retweet_count = retweet_count;
    }

    public Integer getLike_count() {
        return like_count;
    }

    public void setLike_count(Integer like_count) {
        this.like_count = like_count;
    }
}
