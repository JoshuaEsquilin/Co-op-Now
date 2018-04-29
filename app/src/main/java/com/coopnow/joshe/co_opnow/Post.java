package com.coopnow.joshe.co_opnow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Post {

    public String userID;
    public String author;
    public String gameName;
    public String platform;
    public String gamerTag;
    public String description;
    public String numOfPeople;
    public String availability;
    public String location;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String userID, String author, String gameName, String platform, String gamerTag, String descrip, String numOfPeople, String availability, String location) {
        this.userID = userID;
        this.author = author;
        this.gameName = gameName;
        this.platform = platform;
        this.gamerTag = gamerTag;
        this.description = descrip;
        this.numOfPeople = numOfPeople;
        this.availability = availability;
        this.location = location;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userID", userID);
        result.put("author", author);
        result.put("gameName", gameName);
        result.put("description", description);
        result.put("gamerTag", gamerTag);
        result.put("platform", platform);
        result.put("numOfPeople", numOfPeople);
        result.put("availability", availability);
        result.put("location", location);

        return result;
    }
}

