package com.example.cotton.ui.Trading;

public class TradingViewPagerItem {

    String major;
    int registeredBookImage;
    String registeredBookTitle;
    String registeredBookAuthor;

    public void setMajor(String major) {
        this.major = major;
    }

    public void setRegisteredBookImage(int registeredBookImage) {
        this.registeredBookImage = registeredBookImage;
    }

    public void setRegisteredBookTitle(String registeredBookTitle) {
        this.registeredBookTitle = registeredBookTitle;
    }

    public void setRegisteredBookAuthor(String registeredBookAuthor) {
        this.registeredBookAuthor = registeredBookAuthor;
    }

    public String getMajor() {
        return major;
    }

    public int getRegisteredBookImage() {
        return registeredBookImage;
    }

    public String getRegisteredBookTitle() {
        return registeredBookTitle;
    }

    public String getRegisteredBookAuthor() {
        return registeredBookAuthor;
    }
}
