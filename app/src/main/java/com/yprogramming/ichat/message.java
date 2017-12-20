package com.yprogramming.ichat;

import com.google.firebase.database.ServerValue;

import java.security.Timestamp;
import java.util.Map;

/**
 * Created by yourthor on 18/12/2560.
 */

public class message {
    private String mText;
    private String mType;
    private String mDate;
    private String mTime;
    private Map<String, String> timeStamp;
    private boolean viewed;
    private String sender;

    public message(){

    }

    public message(String mText, String mType, String mDate, String mTime, Map<String, String> timeStamp, boolean viewed, String sender) {
        this.mText = mText;
        this.mType = mType;
        this.mDate = mDate;
        this.mTime = mTime;
        this.timeStamp = timeStamp;
        this.viewed = viewed;
        this.sender = sender;
    }

    public String getmText() {
        return mText;
    }

    public String getmType() {
        return mType;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmTime() {
        return mTime;
    }

    public Map<String, String> getTimeStamp() {
        return timeStamp;
    }

    public boolean isViewed() {
        return viewed;
    }

    public String getSender() {
        return sender;
    }
}
