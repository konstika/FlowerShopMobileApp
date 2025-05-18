package com.example.flowershop.entity;

import java.util.List;

public class Order {
    String id;
    String userID;
    String date_order;
    String address;
    String date;
    String time;
    String status;
    List<Product> products;
    public Order(){}

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getUserID(){
        return userID;
    }
    public void setDate_order(String date_order) {
        this.date_order = date_order;
    }
    public String getDate_order() {
        return date_order;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddress() {
        return address;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getDate() {
        return date;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getTime() {
        return time;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
    public void setProducts(List<Product> products) {
        this.products = products;
    }
    public List<Product> getProducts() {
        return this.products;
    }
}
