package com.coopnow.joshe.co_opnow;

import android.app.Fragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
// Date:         5/8/2018
// Description:  Making a Group handles the creation of a post and saving it to the database.
//               After creation, it moves to PostInfo to display the contents.

public class MakingAGroup extends Fragment {

    private DatabaseReference fireDatabase;

    private EditText textGameName;
    private EditText textPlatform;
    private EditText textGamerTag;
    private EditText textDescription;
    private EditText textNumberOfPeople;
    private EditText textTime;
    private EditText textLocation;

    private Button buttonCreateGroup;

    String key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return inflater.inflate(R.layout.activity_making_agroup, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();

        fireDatabase = FirebaseDatabase.getInstance().getReference();

        textGameName = view.findViewById(R.id.name_input);
        textPlatform = view.findViewById(R.id.platform_input);
        textGamerTag = view.findViewById(R.id.gamertag_input);
        textDescription = view.findViewById(R.id.Desc_input);
        textNumberOfPeople = view.findViewById(R.id.numPeople_input);
        textTime = view.findViewById(R.id.time_input);
        textLocation = view.findViewById(R.id.location_input);
        buttonCreateGroup = view.findViewById(R.id.button13);

        buttonCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
    }

    private void submitPost() {
        final String game = textGameName.getText().toString();
        final String plat = textPlatform.getText().toString();
        final String gamerT = textGamerTag.getText().toString();
        final String descr = textDescription.getText().toString();
        final String num = textNumberOfPeople.getText().toString();
        final String time = textTime.getText().toString();
        final String locat = textLocation.getText().toString();

        // Each textField is "Required" to be filled out by the user
        if (TextUtils.isEmpty(game)) {
            textGameName.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(plat)) {
            textPlatform.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(plat)) {
            textGamerTag.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(descr)) {
            textDescription.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(num)) {
            textNumberOfPeople.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(time)) {
            textTime.setError("Required");
            return;
        }
        if (TextUtils.isEmpty(locat)) {
            textLocation.setError("Required");
            return;
        }

        // Disable button so there are no multi-posts from the user
        setEditingEnabled(false);
        Toast.makeText(getActivity(), "Submitting Post...", Toast.LENGTH_SHORT).show();

        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fireDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) {
                            // User is null, error out since the user could not be found
                            Log.e("MakingAGroup", "User " + userId + " is null for an unknown reason");
                            Toast.makeText(getActivity(),
                                    "Error: could not find user in database.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write a new post to the database
                            writeNewPost(userId, user.username, game, plat, gamerT, descr, num, time, locat);
                        }

                        // Finish this Activity,and display the indo in PostInfo
                        setEditingEnabled(true);
                        Intent intent =  new Intent(getActivity(), PostInfo.class);
                        intent.putExtra("post_key", key);
                        startActivity(intent);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("MakingAGroup", "getUser:onCancelled", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });
    }

    private void setEditingEnabled(boolean enabled) {
        textGameName.setEnabled(enabled);
        textPlatform.setEnabled(enabled);
        textGamerTag.setEnabled(enabled);
        textDescription.setEnabled(enabled);
        textNumberOfPeople.setEnabled(enabled);
        textTime.setEnabled(enabled);
        textLocation.setEnabled(enabled);

        if (enabled) {
            buttonCreateGroup.setVisibility(View.VISIBLE);
        } else {
            buttonCreateGroup.setVisibility(View.GONE);
        }
    }

    private void writeNewPost(String userId, String username, String gameTitle, String gamePlat, String gamerT, String description, String number, String availability, String location) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously in the database
        key = fireDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, gameTitle, gamePlat, gamerT, description, number, availability, location);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        fireDatabase.updateChildren(childUpdates);
    }
}
