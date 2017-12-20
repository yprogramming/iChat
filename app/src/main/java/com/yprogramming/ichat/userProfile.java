package com.yprogramming.ichat;

/**
 * Created by yourthor on 6/11/2560.
 */

public class userProfile {
    private String fullname;
    private String gender;
    private String status;
    private String profileUrl;

    public userProfile(){

    }
    public userProfile(String fullname, String gender, String status, String profileUrl){
        this.fullname = fullname;
        this.gender = gender;
        this.status = status;
        this.profileUrl = profileUrl;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getFullname() {
        return fullname;
    }

    public String getGender() {
        return gender;
    }

    public String getStatus() {
        return status;
    }

    public String getProfileUrl() {
        return profileUrl;
    }
}
