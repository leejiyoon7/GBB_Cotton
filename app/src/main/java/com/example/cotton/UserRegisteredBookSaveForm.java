package com.example.cotton;

public class UserRegisteredBookSaveForm {
    private String bookName;
    private String bookWriter;

    public UserRegisteredBookSaveForm(String bookName, String bookWriter) {
        this.bookName = bookName;
        this.bookWriter = bookWriter;
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
}
