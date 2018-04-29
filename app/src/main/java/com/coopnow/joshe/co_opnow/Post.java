package com.coopnow.joshe.co_opnow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Post implements Serializable{

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

    public Post(Map<String, Object> map){
        this.userID = map.get("userID").toString();
        this.author = map.get("author").toString();
        this.gameName = map.get("gameName").toString();
        this.platform = map.get("description").toString();
        this.gamerTag = map.get("gamerTag").toString();
        this.description = map.get("platform").toString();
        this.numOfPeople = map.get("numOfPeople").toString();
        this.availability = map.get("availability").toString();
        this.location = map.get("location").toString();
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

    public String getUserID(){
        return this.userID;
    }

    public String getAuthor(){
        return this.author;
    }

    public String getGameName(){
        return this.gameName;
    }

    public String getPlatform(){
        return this.platform;
    }

    public String getDescription(){
        return this.description;
    }

    public String getNumOfPeople(){
        return this.numOfPeople;
    }

    public String getAvailability(){
        return this.availability;
    }

    public String getLocation(){
        return this.location;
    }
}

