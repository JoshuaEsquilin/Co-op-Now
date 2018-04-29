package com.coopnow.joshe.co_opnow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class PostGallery extends AppCompatActivity {

    private EditText searchQuery;
    private Button searchButton;
    private Switch toggleSelfPostsOnly;
    private DatabaseReference ref;
    private PostAdapter postAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Post> allPosts;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MakingAGroup.class));
            }
        });

        allPosts = new ArrayList<>();

        sharedPrefs = getSharedPreferences("username", Context.MODE_PRIVATE);

        searchQuery = findViewById(R.id.editText_SearchQuery);
        searchButton = findViewById(R.id.button_SearchPosts);
        toggleSelfPostsOnly = findViewById(R.id.switch_SelfPostOnly);

        recyclerView = findViewById(R.id.recyclerView_Posts);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        postAdapter = new PostAdapter(getApplicationContext(), getAllPosts());
        recyclerView.setAdapter(postAdapter);

        ref = FirebaseDatabase.getInstance().getReference().child("posts");

        setupListeners();
    }

    private void setupListeners(){
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchQuery.getText().toString().equals("")){
                    postAdapter.update(getAllPosts());
                } else {
                    postAdapter.update(getAllPosts(searchQuery.getText().toString()));
                }
            }
        });
        toggleSelfPostsOnly.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    postAdapter.update(getAllUserPosts());
                } else{
                    postAdapter.update(getAllPosts());
                }
            }
        });
        ref.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        extractPosts((Map<String,Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // handle error
                    }
                });
    }

    private void extractPosts(Map<String,Object> postsMap){
        ArrayList<Post> posts = new ArrayList<>();
        for (Map.Entry<String, Object> entry : postsMap.entrySet()){
            Map singlePost = (Map) entry.getValue();
            posts.add(new Post(singlePost));
        }
        allPosts = posts;
        postAdapter.update(allPosts);
    }

    private ArrayList getAllPosts(){
        return allPosts;
    }

    private ArrayList getAllPosts(String query){
        ArrayList<Post> searchedPosts = new ArrayList<>();
        for (Post post: allPosts){
            if(post.getGameName().equals(query)){
                searchedPosts.add(post);
            }
        }
        return searchedPosts;
    }

    private ArrayList getAllUserPosts(){
        ArrayList<Post> searchedPosts = new ArrayList<>();
        String username = sharedPrefs.getString("username", null);
        for (Post post: allPosts){
            if(post.getAuthor().equals(username)){
                searchedPosts.add(post);
            }
        }
        return searchedPosts;
    }
}
