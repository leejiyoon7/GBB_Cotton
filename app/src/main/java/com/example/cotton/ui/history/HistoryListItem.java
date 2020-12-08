package com.example.cotton.ui.history;

public class HistoryListItem {

    private int historyIcon;
    private String historyType;
    private String historyBookname;
    private String historyDate;
    private String historyVariance;

    public void setHistoryIcon(int historyIcon) {
        this.historyIcon = historyIcon;
    }

    public void setHistoryType(String historyType) {
        this.historyType = historyType;
    }

    public void setHistoryBookname(String historyBookname) {
        this.historyBookname = historyBookname;
    }

    public void setHistoryDate(String historyDate) {
        this.historyDate = historyDate;
    }

    public void setHistoryVariance(String historyVariance) {
        this.historyVariance = historyVariance;
    }


    public int getHistoryIcon() {
        return historyIcon;
    }

    public String getHistoryType() {
        return historyType;
    }

    public String getHistoryBookname() {
        return historyBookname;
    }

    public String getHistoryDate() {
        return historyDate;
    }

    public String getHistoryVariance() {
        return historyVariance;
    }


}
