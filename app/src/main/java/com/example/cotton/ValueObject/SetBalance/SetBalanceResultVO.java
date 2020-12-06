package com.example.cotton.ValueObject.SetBalance;

public class SetBalanceResultVO {

    private String result;
    private FoodBuyVO data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public FoodBuyVO getDataFoodBuy() {
        return data;
    }

    public void setDataFoodBuy(FoodBuyVO data) {
        this.data = data;
    }
}
