package com.example.mychatapp;

public class Message {

    String message;
    String from;
    String imageUrl;

    public Message(String from, String message){
        this.message = message;
        this.from = from;
    }


    public Message(String from, String message, String imageUrl){
        this.message = message;
        this.from = from;
        this.imageUrl = imageUrl;
    }

    public Message(){

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String textMessage) {
        this.message = textMessage;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
