package com.coopnow.joshe.co_opnow;

import com.google.firebase.database.IgnoreExtraProperties;

// Author:       Joshua Esquilin, Cody Greene
// Date:         4/30/2018
// Description:  Comment is a model for what should make up a comment on a post in the database

@IgnoreExtraProperties
public class Comment {

    public String userID;
    public String author;
    public String replyText;

    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Comment(String userID, String author, String replyText) {
        this.userID = userID;
        this.author = author;
        this.replyText = replyText;
    }

}

