package com.example.cotton;

public class UserRentedBookSaveForm {
    private String bookName;
    private String bookWriter;
    private String status;
    private String barcode;
    private String bookOwnerUUID;


    public UserRentedBookSaveForm(String bookName, String bookWriter, String status, String bookOwnerUUID) {
        this.bookName = bookName;
        this.bookWriter = bookWriter;
        this.barcode = barcode;
        this.status = status;
        this.bookOwnerUUID = bookOwnerUUID;
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBookOwnerUUID() { return bookOwnerUUID; }
    public void setBookOwnerUUID(String bookOwnerUUID) { this.bookOwnerUUID = bookOwnerUUID; }
}
