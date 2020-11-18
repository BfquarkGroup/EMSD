package com.oufar.emsd.Model;

public class Menu {

    private String delivery;
    private String storeName;
    private String status;
    private String clientEmail;
    private String clientId;
    private String description;
    private String id;
    private String imageURL;
    private String number;
    private String plate;
    private String price;
    private String storeEmail;
    private String storeId;


    public Menu(String delivery, String storeName, String status, String clientEmail, String clientId, String description, String id, String imageURL, String number, String plate, String price, String storeEmail, String storeId) {
        this.delivery = delivery;
        this.storeName = storeName;
        this.status = status;
        this.clientEmail = clientEmail;
        this.clientId = clientId;
        this.description = description;
        this.id = id;
        this.imageURL = imageURL;
        this.number = number;
        this.plate = plate;
        this.price = price;
        this.storeEmail = storeEmail;
        this.storeId = storeId;
    }

    public Menu() {
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStoreEmail() {
        return storeEmail;
    }

    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
