package com.yprogramming.ichat;

import java.util.Map;

/**
 * Created by yourthor on 2/12/2560.
 */

public class post {

    private String imgUrl;
    private String title;
    private String detail;
    private String create_date;
    private String create_time;
    private String author_id;
    private Map<String, String> timeStamp;
    public post(){

    }

    public post(String imgUrl, String title, String detail, String create_date, String create_time, String author_id, Map<String, String> timeStamp){
        this.imgUrl = imgUrl;
        this.title = title;
        this.detail = detail;
        this.create_date = create_date;
        this.create_time = create_time;
        this.author_id = author_id;
        this.timeStamp = timeStamp;
    }



    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public void setTimeStamp(Map<String, String> timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public String getCreate_date() {
        return create_date;
    }

    public String getCreate_time() { return create_time; }

    public String getAuthor_id() {
        return author_id;
    }

    public Map<String, String> getTimeStamp() {
        return timeStamp;
    }
}
