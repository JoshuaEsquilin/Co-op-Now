package com.coopnow.joshe.co_opnow;

import com.google.firebase.database.IgnoreExtraProperties;

// Author:       Joshua Esquilin, Cody Greene
// Date:         4/30/2018
// Description:  User is a model for what should make up a User in the database

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

}

