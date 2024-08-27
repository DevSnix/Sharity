package com.test.sharity;

public class Review {
    private String reviewText;
    private double rating;
    private User user;
    private String date;

    // No-argument constructor required for Firebase
    public Review() {
    }

    // Constructor with arguments
    public Review(String reviewText, double rating, User user, String date) {
        this.reviewText = reviewText;
        this.rating = rating;
        this.user = user;
        this.date = date; // Fix: Set date properly
    }

    // Getters and setters
    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
