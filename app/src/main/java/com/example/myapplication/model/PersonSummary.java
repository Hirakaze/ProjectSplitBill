package com.example.myapplication.model;

public class PersonSummary {
    private String name;
    private double belanja;
    private double extraFee;
    private double grandTotal;

    public PersonSummary(String name, double belanja, double extraFee, double grandTotal) {
        this.name = name;
        this.belanja = belanja;
        this.extraFee = extraFee;
        this.grandTotal = grandTotal;
    }

    public String getName() { return name; }
    public double getBelanja() { return belanja; }
    public double getExtraFee() { return extraFee; }
    public double getGrandTotal() { return grandTotal; }
}