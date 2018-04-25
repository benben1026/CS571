package com.example.weizhou.cs571;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class ItemDetail {
    private String placeId;
    private String name;
    private String address;
    private String phoneNumber;
    private int priceLevel;
    private float rating;
    private String googlePage;
    private String website;
    private ArrayList<Bitmap> photos;
    private ArrayList<Review> googleReviews;
    private ArrayList<Review> yelpReviews;

    ItemDetail(String placeId){
        this.placeId = placeId;
        this.name = null;
        this.address = null;
        this.phoneNumber = null;
        this.priceLevel = -1;
        this.rating = -1;
        this.googlePage = null;
        this.website = null;
        this.photos = new ArrayList<>();
        this.googleReviews = new ArrayList<>();
        this.yelpReviews = new ArrayList<>();
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getAddress(){
        return this.address;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber(){
        return this.phoneNumber;
    }

    public void setPriceLevel(int priceLevel){
        this.priceLevel = priceLevel;
    }

    public int getPriceLevel(){
        return this.priceLevel;
    }

    public void setRating(float rating){
        this.rating = rating;
    }

    public float getRating(){
        return this.rating;
    }

    public void setGooglePage(String googlePage){
        this.googlePage = googlePage;
    }

    public String getGooglePage(){
        return this.googlePage;
    }

    public void setWebsite(String website){
        this.website = website;
    }

    public String getWebsite(){
        return this.website;
    }

    public void addPhoto(Bitmap photo){
        this.photos.add(photo);
    }

    public ArrayList<Bitmap> getPhotos() {
        return photos;
    }

    public ArrayList<Review> getGoogleReviews() {
        return googleReviews;
    }

    public void addGoogleReviews(Review googleReview) {
        this.googleReviews.add(googleReview);
    }

    public ArrayList<Review> getYelpReviews() {
        return yelpReviews;
    }

    public void addYelpReviews(Review yelpReview) {
        this.yelpReviews.add(yelpReview);
    }
}

