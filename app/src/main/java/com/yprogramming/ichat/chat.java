package com.yprogramming.ichat;

/**
 * Created by yourthor on 19/12/2560.
 */

public class chat {
    String relationDate;
    Long timeStamp;

    public chat(){

    }

    public chat(String relationDate, Long timeStamp) {
        this.relationDate = relationDate;
        this.timeStamp = timeStamp;
    }

    public void setRelationDate(String relationDate) {
        this.relationDate = relationDate;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getRelationDate() {
        return relationDate;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

}
