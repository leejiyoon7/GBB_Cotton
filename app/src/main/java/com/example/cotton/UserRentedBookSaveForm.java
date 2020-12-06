package com.example.cotton;

public class UserRentedBookSaveForm {
    private String bookName;
    private String bookWriter;
    private String status;

    public UserRentedBookSaveForm(String bookName, String bookWriter, String status) {
        this.bookName = bookName;
        this.bookWriter = bookWriter;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
