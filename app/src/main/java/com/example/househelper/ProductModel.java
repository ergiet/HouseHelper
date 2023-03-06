package com.example.househelper;

public class ProductModel {
    String product;
    boolean is_bought;

    public ProductModel(String product, boolean is_bought) {
        this.product = product;
        this.is_bought = is_bought;
    }

    public String getProduct() {
        return product;
    }

    public Boolean getIs_bought() {
        return is_bought;
    }

    public void setIs_bought(Boolean is_bought) {
        this.is_bought = is_bought;
    }
}