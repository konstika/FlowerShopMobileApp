package com.example.flowershop;


public class Product {
    String title;
    int imageId;
    int price;
    int id;
    boolean inBasket;
    Product(String title, int imageId, int price, int id, boolean inBasket){
        this.title=title;
        this.imageId=imageId;
        this.price=price;
        this.id=id;
        this.inBasket=inBasket;
    }
    Product(String title, int imageId, int price){
        this.title=title;
        this.imageId=imageId;
        this.price=price;
        this.id=0;
        this.inBasket=false;
    }
    String getTitle(){return title;}
    int getImageId(){return imageId;}
    int getPrice(){return price;}
    boolean getInBasket(){return inBasket;}
    int getId(){return id;}
}
