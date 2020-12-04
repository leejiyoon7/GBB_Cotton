package com.example.cotton;

public class bookSaveForm {
    private String pictureLink;
    private String major;
    private String bookName;
    private String bookWriter;
    private String walletInfo;
    private String userName;


    public bookSaveForm(){}
    public bookSaveForm(String pictureLink, String major, String bookName, String bookWriter, String walletInfo, String userName){
        this.pictureLink = pictureLink;
        this.major = major;
        this.bookName = bookName;
        this.bookWriter = bookWriter;
        this.walletInfo = walletInfo;
        this.userName = userName;
    }

    public String getPictureLink() {
        return pictureLink;
    }

    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookWriter() {
        return bookWriter;
    }

    public void setBookWriter(String bookWriter) {
        this.bookWriter = bookWriter;
    }

    public String getWalletInfo() {
        return walletInfo;
    }

    public void setWalletInfo(String walletInfo) {
        this.walletInfo = walletInfo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
