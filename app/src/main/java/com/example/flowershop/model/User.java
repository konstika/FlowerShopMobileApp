package com.example.flowershop.model;

public class User {
    String id;
    String username;
    String password;
    String phone;
    String basketID;
    public User(){}
    public User(String id, String username, String password, String phone){
        this.id=id;
        this.username=username;
        this.password=password;
        this.phone=phone;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getPhone() {
        return phone;
    }
    public void setBasketID(String basketID) {
        this.basketID = basketID;
    }
    public String getBasketID() {
        return basketID;
    }
}
