package com.coopnow.joshe.co_opnow;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

// Author:       Joshua Esquilin, Cody Greene
// Date:         4/30/2018
// Description:  PostGallery handles the production of a list entry of a post from users in the app

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder>{

    private ArrayList<Post> data;
    private ArrayList<String> keys;
    private Context context;

    public PostAdapter(Context context, ArrayList<Post> data){
        this.context = context;
        this.data = data;
    }

    @Override
    public int getItemCount(){
        return this.data.size();
    }

    @Override
    public PostHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item__post, viewGroup, false);
        return new PostHolder(view, context, data);
    }

    @Override
    public void onBindViewHolder(PostHolder postHolder, final int i){
        postHolder.gameName.setText(data.get(i).getGameName());
        postHolder.postDesc.setText(data.get(i).getDescription());
        postHolder.platform.setText(data.get(i).getPlatform());
        postHolder.availability.setText(data.get(i).getAvailability());
        postHolder.numOfPeople.setText(data.get(i).getNumOfPeople());

        postHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostInfo.class);
                intent.putExtra("post_key", data.get(i).getKey());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void update(ArrayList<Post> newData){
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    public class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView gameName;
        TextView postDesc;
        TextView availability;
        TextView numOfPeople;
        TextView platform;
        Context context;
        ArrayList<Post> posts;

        PostHolder(View view, Context context, ArrayList posts){
            super(view);
            this.context = context;
            this.posts = posts;
            view.setOnClickListener(this);
            gameName = view.findViewById(R.id.textView_GameName);
            postDesc = view.findViewById(R.id.textView_PostDesc);
            availability = view.findViewById(R.id.textView_Availability);
            platform = view.findViewById(R.id.textView_Platform);
            numOfPeople = view.findViewById(R.id.textView_numOfPeople);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, PostInfo.class);
            intent.putExtra("post_key", this.posts.get(getLayoutPosition()).getKey());
            this.context.startActivity(intent);
        }
    }
}
