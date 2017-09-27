package com.chives.serve.backend.models;

/**
 * Created by aditya on 4/11/17.
 */
public class Client {
    public int restaurantId;
    public int echoId;

    public Client() {

    }

    public Client(int restaurantId, int echoId) {
        this.restaurantId = restaurantId;
        this.echoId = echoId;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public int getEchoId() {
        return echoId;
    }

    public void setEchoId(int echoId) {
        this.echoId = echoId;
    }

    @Override
    public int hashCode(){
        return (int) (Math.pow(restaurantId, 2) + Math.pow(echoId, 4));
    }

    @Override
    public boolean equals(Object obj) {
        Client c = (Client) obj;
        return ((this.echoId == c.echoId) && (this.restaurantId == c.restaurantId));
    }
}
