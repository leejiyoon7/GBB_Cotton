package com.example.cotton.ui.history;

import android.util.Log;

import com.example.cotton.LogForm;

import java.util.Comparator;

public class DateCompare implements Comparator<LogForm> {

    @Override
    public int compare(LogForm logForm1, LogForm logForm2) {

        int compare=logForm1.getDate().compareTo(logForm2.getDate());
        Log.d("시간 비교","logForm1.getDate(): "+logForm1.getDate());
        Log.d("시간 비교","logForm2.getDate(): "+logForm2.getDate());
        Log.d("시간 비교","compare: "+compare);
        if(compare>0){
            return 1;
        }
        else if(compare<0){
            return -1;
        }
        else{
            return 0;
        }
    }
}