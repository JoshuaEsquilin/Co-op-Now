package com.coopnow.joshe.co_opnow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    private Button makeGroupButton;
    private Button seeAllPostsButton;
    private Button seeAllMyPostsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        makeGroupButton = findViewById(R.id.button_make_a_group);
        seeAllPostsButton= findViewById(R.id.button_see_all_posts);
        seeAllMyPostsButton = findViewById(R.id.button_see_all_my_posts);

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
                startActivity(new Intent(MainMenu.this, ListofPosts.class));
                finish();
            }
        });

        seeAllMyPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenu.this, MyPosts.class));
                finish();
            }
        });
    }


}
