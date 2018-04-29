package com.coopnow.joshe.co_opnow;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder>{

    private ArrayList<Post> entries;
    private Context context;

    public PostAdapter(Context context, ArrayList<Post> entries){
        this.context = context;
        this.entries = entries;
    }

    @Override
    public int getItemCount(){
        return this.entries.size();
    }

    @Override
    public PostHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item__post, viewGroup, false);
        return new PostHolder(view, context, entries);
    }

    @Override
    public void onBindViewHolder(PostHolder postHolder, final int i){
        postHolder.gameName.setText(entries.get(i).getGameName());
        postHolder.postDesc.setText(entries.get(i).getDescription());
        postHolder.availability.setText(entries.get(i).getAvailability());
        postHolder.numOfPeople.setText(entries.get(i).getNumOfPeople());

        postHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostInfo.class);
                intent.putExtra("Post", entries.get(i));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView gameName;
        TextView postDesc;
        TextView availability;
        TextView numOfPeople;
        Context context;
        ArrayList posts;

        PostHolder(View view, Context context, ArrayList posts){
            super(view);
            this.context = context;
            this.posts = posts;
            view.setOnClickListener(this);
            gameName = view.findViewById(R.id.textView_GameName);
            postDesc = view.findViewById(R.id.textView_PostDesc);
            availability = view.findViewById(R.id.textView_Availability);
            numOfPeople = view.findViewById(R.id.textView_numOfPeople);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, PostInfo.class);
            intent.putExtra("Post", (Post)this.posts.get(getLayoutPosition()));
            this.context.startActivity(intent);
        }
    }
}
