package com.yprogramming.ichat;

/**
 * Created by yourthor on 19/12/2560.
 */

public class vMessage {
    private String mText;
    private String mType;
    private String mDate;
    private String mTime;
    private Long timeStamp;
    private boolean viewed;
    private String sender;

    public vMessage(){

    }

    public vMessage(String mText, String mType, String mDate, String mTime, Long timeStamp, boolean viewed, String sender) {
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

    public Long getTimeStamp() {
        return timeStamp;
    }

    public boolean isViewed() {
        return viewed;
    }

    public String getSender() {
        return sender;
    }
}
