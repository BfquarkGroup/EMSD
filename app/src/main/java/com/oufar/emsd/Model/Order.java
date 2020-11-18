package com.oufar.emsd.Model;

public class Order {

    private String id;
    private String clientId;
    private String clientName;
    private String clientPhone;
    private String clientAddress;
    private String clientImageURL;
    private String lat;
    private String lng;
    private String orderNumber;
    private String storeId;
    private String storeName;
    private String storeImageURL;
    private String storeLat;
    private String storeLng;
    private String delivery;

    public Order(String id, String clientId, String clientName, String clientPhone,
                 String clientAddress, String clientImageURL, String lat, String lng,
                 String orderNumber, String storeId, String storeName,
                 String delivery, String storeImageURL, String storeLat, String storeLng) {
        this.id = id;
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.clientAddress = clientAddress;
        this.clientImageURL = clientImageURL;
        this.lat = lat;
        this.lng = lng;
        this.orderNumber = orderNumber;
        this.storeId = storeId;
        this.storeName = storeName;
        this.delivery = delivery;
        this.storeImageURL = storeImageURL;
        this.storeLat = storeLat;
        this.storeLng = storeLng;
    }

    public Order() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getClientImageURL() {
        return clientImageURL;
    }

    public void setClientImageURL(String clientImageURL) {
        this.clientImageURL = clientImageURL;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getStoreImageURL() {
        return storeImageURL;
    }

    public void setStoreImageURL(String storeImageURL) {
        this.storeImageURL = storeImageURL;
    }

    public String getStoreLat() {
        return storeLat;
    }

    public void setStoreLat(String storeLat) {
        this.storeLat = storeLat;
    }

    public String getStoreLng() {
        return storeLng;
    }

    public void setStoreLng(String storeLng) {
        this.storeLng = storeLng;
    }
}
