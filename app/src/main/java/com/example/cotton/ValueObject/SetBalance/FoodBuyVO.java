package com.example.cotton.ValueObject.SetBalance;

public class FoodBuyVO {

    private String txId;
    private String reqTs;
    private String txUuid;

    public String getTxId() {
        return txId;
    }

    public String getTxUuid() {
        return txUuid;
    }

    public void setTxUuid(String txUuid) {
        this.txUuid = txUuid;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getReqTs() {
        return reqTs;
    }

    public void setReqTs(String reqTs) {
        this.reqTs = reqTs;
    }
}

