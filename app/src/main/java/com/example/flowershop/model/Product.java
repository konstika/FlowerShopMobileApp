package com.example.flowershop.model;


import java.util.List;

public class Product implements Comparable<Product>{
    String id;
    String name;
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

    @Override
    public int compareTo(Product o) {
        return Integer.compare(this.price, o.getPrice());
    }
}
