package com.example.myapplication.model;

import java.util.List;
import java.util.Map;

public class Transaction {
    private String id;
    private String restaurantName;
    private String date;
    private long grandTotal;
    private List<Map<String, Object>> participants; // ringkasan patungan tiap orang

    // Konstruktor Kosong diperlukan untuk Firebase Firestore
    public Transaction() {}

    public Transaction(String id, String restaurantName, String date, long grandTotal, List<Map<String, Object>> participants) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.date = date;
        this.grandTotal = grandTotal;
        this.participants = participants;
    }

    // Getter Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public long getGrandTotal() { return grandTotal; }
    public void setGrandTotal(long grandTotal) { this.grandTotal = grandTotal; }

    public List<Map<String, Object>> getParticipants() { return participants; }
    public void setParticipants(List<Map<String, Object>> participants) { this.participants = participants; }
}