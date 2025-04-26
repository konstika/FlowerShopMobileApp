package com.example.flowershop;

public class Basket {
    int id;
    int idUser;
    int idProduct;
    int count;
    public Basket(int id, int idUser, int idProduct, int count){
        this.id=id;
        this.idUser=idUser;
        this.idProduct=idProduct;
        this.count=count;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public int getIdUser() {
        return idUser;
    }
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
    public int getIdProduct() {
        return idProduct;
    }
    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
}
