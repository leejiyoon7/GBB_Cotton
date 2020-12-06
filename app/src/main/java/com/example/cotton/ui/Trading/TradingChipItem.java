package com.example.cotton.ui.Trading;

public class TradingChipItem {
    String tradingBookTitle;
    String tradingBookAuthor;

    TradingChipItem(){
        tradingBookTitle="";
        tradingBookAuthor="";
    }

    public void setTradingBookTitle(String tradingBookTitle) {
        this.tradingBookTitle = tradingBookTitle;
    }

    public void setTradingBookAuthor(String tradingBookAuthor) {
        this.tradingBookAuthor = tradingBookAuthor;
    }

    public String getTradingBookTitle() {
        return tradingBookTitle;
    }

    public String getTradingBookAuthor() {
        return tradingBookAuthor;
    }
}
