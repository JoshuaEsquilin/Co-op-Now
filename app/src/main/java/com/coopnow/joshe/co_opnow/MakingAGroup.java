package com.coopnow.joshe.co_opnow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

// Author:       Joshua Esquilin, Cody Greene
// Date:         4/30/2018
// Description:  Making a Group handles the creation of a post and saving it to the database.
//               After creation, it moves to PostInfo to display the contents.

public class MakingAGroup extends AppCompatActivity {

    private static final String TAG = "MakingAGroup";
    private static final String REQUIRED = "Required";

    private DatabaseReference mDatabase;

    private EditText mGameName;
    private EditText mPlatform;
    private EditText mGamerTag;
    private EditText mDescription;
    private EditText mNumberOfPeople;
    private EditText mTime;
    private EditText mLocation;

    private Button mCreateGroup;

    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_making_agroup);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mGameName = findViewById(R.id.name_input);
        mPlatform = findViewById(R.id.platform_input);
        mGamerTag = findViewById(R.id.gamertag_input);
        mDescription = findViewById(R.id.Desc_input);
        mNumberOfPeople = findViewById(R.id.numPeople_input);
        mTime = findViewById(R.id.time_input);
        mLocation = findViewById(R.id.location_input);
        mCreateGroup = findViewById(R.id.button13);

        mCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
    }

    private void submitPost() {
        final String game = mGameName.getText().toString();
        final String plat = mPlatform.getText().toString();
        final String gamerT = mGamerTag.getText().toString();
        final String descr = mDescription.getText().toString();
        final String num = mNumberOfPeople.getText().toString();
        final String time = mTime.getText().toString();
        final String locat = mLocation.getText().toString();

        // Each textField is required to be filled out by the user
        if (TextUtils.isEmpty(game)) {
            mGameName.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(plat)) {
            mPlatform.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(plat)) {
            mGamerTag.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(descr)) {
            mDescription.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(num)) {
            mNumberOfPeople.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(time)) {
            mTime.setError(REQUIRED);
            return;
        }
        if (TextUtils.isEmpty(locat)) {
            mLocation.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts from the user
        setEditingEnabled(false);
        Toast.makeText(this, "Submitting Post...", Toast.LENGTH_SHORT).show();

        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            // User is null, error out since the user could not be found
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(MakingAGroup.this,
                                    "Error: could not find user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write a new post to the database
                            writeNewPost(userId, user.username, game, plat, gamerT, descr, num, time, locat);
                        }

                        // Finish this Activity,and display the indo in PostInfo
                        setEditingEnabled(true);
                        Intent intent =  new Intent(MakingAGroup.this, PostInfo.class);
                        intent.putExtra(PostInfo.EXTRA_POST_KEY, key);
                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });
    }

    private void setEditingEnabled(boolean enabled) {
        mGameName.setEnabled(enabled);
        mPlatform.setEnabled(enabled);
        mGamerTag.setEnabled(enabled);
        mDescription.setEnabled(enabled);
        mNumberOfPeople.setEnabled(enabled);
        mTime.setEnabled(enabled);
        mLocation.setEnabled(enabled);

        if (enabled) {
            mCreateGroup.setVisibility(View.VISIBLE);
        } else {
            mCreateGroup.setVisibility(View.GONE);
        }
    }

    private void writeNewPost(String userId, String username, String gameTitle, String gamePlat, String gamerT, String description, String number, String availability, String location) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously in the database
        key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, gameTitle, gamePlat, gamerT, description, number, availability, location);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }
}
