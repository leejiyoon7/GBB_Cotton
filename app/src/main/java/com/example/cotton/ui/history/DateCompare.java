package com.example.cotton.ui.history;

import android.util.Log;

import com.example.cotton.LogForm;

import java.util.Comparator;

public class DateCompare implements Comparator<LogForm> {
    int state=1;

    @Override
    public int compare(LogForm logForm1, LogForm logForm2) {

        int compare=logForm1.getDate().compareTo(logForm2.getDate());
        if(compare>0){
            return state;
        }
        else if(compare<0){
            return -1*state;
        }
        else{
            return 0;
        }
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}