package com.coopnow.joshe.co_opnow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

// Author:       Joshua Esquilin
// Date:         4/30/2018
// Description:  MainMenu handles the buttons navigation to make a group or see all posts

public class MainMenu extends AppCompatActivity {

    private Button makeGroupButton;
    private Button seeAllPostsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        makeGroupButton = findViewById(R.id.button_make_a_group);
        seeAllPostsButton= findViewById(R.id.button_see_all_posts);

        makeGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenu.this, MakingAGroup.class));
                finish();
            }
        });

       seeAllPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenu.this, PostGallery.class));
                finish();
            }
        });
    }


}
