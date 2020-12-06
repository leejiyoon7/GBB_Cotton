package com.example.cotton.ValueObject.GetBalance;

public class GetBalanceResultVO {

    private String result;
    private BalanceInfoVO data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public BalanceInfoVO getDataBalance() {
        return data;
    }

    public void setDataBalance(BalanceInfoVO data) {
        this.data = data;
    }
}
