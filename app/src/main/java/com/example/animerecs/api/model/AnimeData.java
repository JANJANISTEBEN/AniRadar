package com.example.animerecs.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AnimeData {

    @SerializedName("mal_id")
    private int id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("synopsis")
    private String synopsis;
    
    @SerializedName("type")
    private String type;
    
    @SerializedName("episodes")
    private int episodes;
    
    @SerializedName("score")
    private double score;
    
    @SerializedName("aired")
    private AiredDate aired;
    
    @SerializedName("genres")
    private List<Genre> genres = new ArrayList<>();
    
    @SerializedName("images")
    private Images images;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("url")
    private String url;
    
    // Constructor
    public AnimeData() {
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSynopsis() {
        return synopsis;
    }
    
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public int getEpisodes() {
        return episodes;
    }
    
    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }
    
    public double getScore() {
        return score;
    }
    
    public void setScore(double score) {
        this.score = score;
    }
    
    public AiredDate getAired() {
        return aired;
    }
    
    public void setAired(AiredDate aired) {
        this.aired = aired;
    }
    
    public List<Genre> getGenres() {
        return genres;
    }
    
    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
    
    public Images getImages() {
        return images;
    }
    
    public void setImages(Images images) {
        this.images = images;
    }
    
    public String getStatus() {
        return status != null ? status : "Unknown";
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getUrl() {
        return url != null ? url : "";
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    // Helper methods
    public String getGenresString() {
        StringBuilder genresBuilder = new StringBuilder();
        for (int i = 0; i < genres.size(); i++) {
            genresBuilder.append(genres.get(i).getName());
            if (i < genres.size() - 1) {
                genresBuilder.append(", ");
            }
        }
        return genresBuilder.toString();
    }
    
    public String getImageUrl() {
        if (images != null && images.getJpg() != null) {
            return images.getJpg().getImageUrl();
        }
        return "";
    }
    
    public String getAiredString() {
        if (aired != null && aired.getString() != null) {
            return aired.getString();
        }
        return "Unknown";
    }
    
    // Nested classes
    public static class AiredDate {
        @SerializedName("string")
        private String string;
        
        public String getString() {
            return string;
        }
        
        public void setString(String string) {
            this.string = string;
        }
    }
    
    public static class Genre {
        @SerializedName("mal_id")
        private int id;
        
        @SerializedName("name")
        private String name;
        
        public int getId() {
            return id;
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
    
    public static class Images {
        @SerializedName("jpg")
        private ImageData jpg;
        
        public ImageData getJpg() {
            return jpg;
        }
        
        public void setJpg(ImageData jpg) {
            this.jpg = jpg;
        }
    }
    
    public static class ImageData {
        @SerializedName("image_url")
        private String imageUrl;
        
        @SerializedName("large_image_url")
        private String largeImageUrl;
        
        public String getImageUrl() {
            return imageUrl;
        }
        
        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
        
        public String getLargeImageUrl() {
            return largeImageUrl;
        }
        
        public void setLargeImageUrl(String largeImageUrl) {
            this.largeImageUrl = largeImageUrl;
        }
    }
} 