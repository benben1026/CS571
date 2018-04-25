package com.example.weizhou.cs571;

import java.util.Comparator;

public class Review {
    public String authorName;
    public String authorImg;
    public float rating;
    public Long dateTime;
    public String comment;
    public String url;

    public static Comparator<Review> highestRating = new Comparator<Review>() {
        @Override
        public int compare(Review o1, Review o2) {
            return o2.rating - o1.rating > 0 ? 1 : -1;
        }
    };

    public static Comparator<Review> lowestRating = new Comparator<Review>() {
        @Override
        public int compare(Review o1, Review o2) {
            return o1.rating - o2.rating > 0 ? 1 : -1;
        }
    };

    public static Comparator<Review> mostRecent = new Comparator<Review>() {
        @Override
        public int compare(Review o1, Review o2) {
            return o2.dateTime - o1.dateTime > 0 ? 1 : -1;
        }
    };

    public static Comparator<Review> leastRecent = new Comparator<Review>() {
        @Override
        public int compare(Review o1, Review o2) {
            return o1.dateTime - o2.dateTime > 0 ? 1 : -1;
        }
    };
}
