package com.idk.foodyrestaurant.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class FoodMenu {

    private String name,price,id, used_id;
    private @ServerTimestamp
    Date timestamp;

    public FoodMenu() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsed_id() {
        return used_id;
    }

    public void setUsed_id(String used_id) {
        this.used_id = used_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
