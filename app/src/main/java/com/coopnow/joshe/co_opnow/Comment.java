package com.coopnow.joshe.co_opnow;

import com.google.firebase.database.IgnoreExtraProperties;

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

