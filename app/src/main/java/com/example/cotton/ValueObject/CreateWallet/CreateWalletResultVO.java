package com.example.cotton.ValueObject.CreateWallet;

public class CreateWalletResultVO {

    private String result;
    private WalletInfoVO data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public WalletInfoVO getDataCreateWallet() {
        return data;
    }

    public void setDataCreateWallet(WalletInfoVO data) {
        this.data = data;
    }
}
