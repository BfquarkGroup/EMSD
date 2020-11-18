package com.oufar.emsd.Model;

public class Info {

    private String username;
    private String address;
    private String delivery;
    private String description;
    private String imageURL;

    public Info(String username, String address, String delivery, String description, String imageURL) {
        this.username = username;
        this.address = address;
        this.delivery = delivery;
        this.description = description;
        this.imageURL = imageURL;
    }

    public Info() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
