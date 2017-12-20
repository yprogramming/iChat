package com.yprogramming.ichat;

import android.app.ProgressDialog;

import java.util.Random;

/**
 * Created by yourthor on 28/10/2560.
 */

public class variable {
    public static boolean checkProfile = false;
    public static boolean checkMenu = false;
    public static String userProfileUrl = "";
    public static String user_gender = "";

    public static String randomProfileName(){
        Random nameGenerator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = nameGenerator.nextInt(50);
        char tempChar;
        for (int i=0; i<randomLength; i++){
            tempChar = (char)(nameGenerator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
