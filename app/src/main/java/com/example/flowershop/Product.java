package com.example.flowershop;


public class Product {
    String title;
    int imageId;
    int price;
    int id;
    int count;

    Product(String title, int imageId, int price, int id, int count){
        this.title=title;
        this.imageId=imageId;
        this.price=price;
        this.id=id;
        this.count=count;
    }
    Product(String title, int imageId, int price){
        this.title=title;
        this.imageId=imageId;
        this.price=price;
        this.id=0;
        this.count=0;
    }
    public String getTitle(){return title;}
    public int getImageId(){return imageId;}
    public int getPrice(){return price;}
    public boolean inBasket(){return count>0;}
    public int getId(){return id;}

    public void setCount(int count) {
        this.count = count;
    }
    public int getCount() {
        return count;
    }
    public void addCount(int k){
        this.count+=k;
    }
}
