package com.example.cotton.ui.food;

//listview items getter setter
public class FoodListItem {

    private int productIcon;
    private String productType;
    private String price;

    public void setPrice(String price) {
        this.price = price;
    }

    public void setProductIcon(int productIcon) {
        this.productIcon = productIcon;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public int getProductIcon() {
        return this.productIcon;
    }

    public String getPrice() {
        return this.price;
    }

    public String getProductType() {
        return this.productType;
    }
}
