package com.example.firebase3.model;

public class Food {
    private int id;
    private String name;
    private int price;
    private String category;
    private String[] image;
    private String description;

    public Food(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public Food(int id, String name, int price, String category, String[] image, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.image = image;
        this.description = description;
    }

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

    public float getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String[] getImage() {
        return image;
    }

    public void setImage(String[] image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
