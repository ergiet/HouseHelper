package com.example.househelper;

import com.google.firebase.Timestamp;

public class ShoppingListModel {
    String username, name;
    Timestamp date;
    boolean is_private;

    public ShoppingListModel(String username, Timestamp date, boolean is_private, String name) {
        this.username = username;
        this.date = date;
        this.is_private = is_private;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public Timestamp getDate() {
        return date;
    }
    public Boolean getIs_private() {
        return is_private;
    }

}