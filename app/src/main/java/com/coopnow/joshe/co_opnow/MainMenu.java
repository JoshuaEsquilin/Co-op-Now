package com.coopnow.joshe.co_opnow;

import android.app.Fragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

// Author:       Joshua Esquilin and Cody Greene
// Date:         5/8/2018
// Description:  MainMenu handles the buttons navigation to make a group or see all posts

public class MainMenu extends Fragment {

    private Button makeGroupButton;
    private Button seeAllPostsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main_menu);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return inflater.inflate(R.layout.activity_main_menu, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();

        makeGroupButton = view.findViewById(R.id.button_make_a_group);
        seeAllPostsButton= view.findViewById(R.id.button_see_all_posts);

        makeGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(getActivity(), MakingAGroup.class));
                Fragment replaceMeWith = new MakingAGroup();
                getActivity().getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, replaceMeWith)
                        .addToBackStack(null)
                        .commit();
            }
        });

        seeAllPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(getActivity(), PostGallery.class));
                Fragment replaceMeWith = new PostGallery();
                getActivity().getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, replaceMeWith)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

}
