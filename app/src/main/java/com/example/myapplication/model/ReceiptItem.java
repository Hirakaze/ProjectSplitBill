package com.example.myapplication.model;

import java.util.ArrayList;
import java.util.List;

public class ReceiptItem {
    private String name;
    private long price;
    private List<String> sharedWith;

    public ReceiptItem(String name, long price) {
        this.name = name;
        this.price = price;
        this.sharedWith = new ArrayList<>();
    }

    public String getName() { return name; }
    public long getPrice() { return price; }
    public List<String> getSharedWith() { return sharedWith; }

    public void addFriend(String name) {
        if (!sharedWith.contains(name)) {
            sharedWith.add(name);
        }
    }

    public void removeFriend(String name) {
        sharedWith.remove(name);
    }
}