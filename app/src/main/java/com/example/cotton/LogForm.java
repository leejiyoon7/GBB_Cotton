package com.example.cotton;

import java.util.Locale;

public class LogForm {
    private String from;
    private String to;
    private String message;
    private String category;
    private String amount;
    private String date;

    public LogForm(String from, String to, String message, String category, String amount, String date)
    {
        this.from = from;
        this.to =to;
        this.message = message;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}