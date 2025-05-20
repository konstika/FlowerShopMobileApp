package com.example.flowershop.model;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Order {
    String id;
    String userID;
    Timestamp date_order;
    String address;
    Timestamp date_delivery;
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

    public void setDate_order(Timestamp date_order) {this.date_order = date_order;}
    public Timestamp getDate_order() {
        return date_order;
    }
    public String getStrDate_order() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date date = date_order.toDate();
        return dateFormat.format(date);
    }

    public void setDate_delivery(Timestamp date_delivery) {this.date_delivery = date_delivery;}
    public void setDate_delivery(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();
        this.date_delivery = new Timestamp(date);
    }
    public Timestamp getDate_delivery() {
        return date_delivery;
    }
    public String getStrDate_delivery() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Date date = date_delivery.toDate();
        return dateFormat.format(date);
    }
    public String getStrTime_delivery() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = date_delivery.toDate();
        return timeFormat.format(date);
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddress() {
        return address;
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
