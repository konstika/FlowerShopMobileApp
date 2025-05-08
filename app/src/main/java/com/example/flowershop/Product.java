package com.example.flowershop;


public class Product {
    String id;
    String name;
    String description;
    int price;
    String imageURL;
    int count;//доп поле, в записи бд product его нет, извлекается из basket или order

    public Product(){}

    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public int getPrice() {
        return price;
    }
    public String getImageURL() {
        return imageURL;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public int getCount() {
        return count;
    }
    public boolean inBasket(){
        return count>0;
    }
    public void addCount(int i){
        this.count+=i;
    }
    public int getSum(){
        return count*price;
    }
}
