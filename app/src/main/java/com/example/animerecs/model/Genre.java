package com.example.animerecs.model;

public enum Genre {
    ACTION("Action", 1),
    ADVENTURE("Adventure", 2),
    COMEDY("Comedy", 4),
    DRAMA("Drama", 8),
    FANTASY("Fantasy", 10),
    HORROR("Horror", 14),
    MYSTERY("Mystery", 7),
    ROMANCE("Romance", 22),
    SCI_FI("Sci-Fi", 24),
    SLICE_OF_LIFE("Slice of Life", 36),
    SPORTS("Sports", 30),
    SUPERNATURAL("Supernatural", 37),
    THRILLER("Thriller", 41);
    
    private final String value;
    private final int id;
    
    Genre(String value, int id) {
        this.value = value;
        this.id = id;
    }
    
    public String getValue() {
        return value;
    }
    
    public int getId() {
        return id;
    }
    
    public static Genre fromValue(String value) {
        for (Genre genre : values()) {
            if (genre.value.equalsIgnoreCase(value)) {
                return genre;
            }
        }
        return null;
    }
    
    public static Genre fromId(int id) {
        for (Genre genre : values()) {
            if (genre.id == id) {
                return genre;
            }
        }
        return null;
    }
} 