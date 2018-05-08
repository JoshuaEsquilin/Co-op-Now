package com.coopnow.joshe.co_opnow;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

// Author:       Joshua Esquilin, Cody Greene
// Date:         5/8/2018
// Description:  PostInfo handles showing the details of a post when a user wants to view one.
//               It also handles comments from users that are saved alongside posts.

public class PostInfo  extends AppCompatActivity implements View.OnClickListener{

    private DatabaseReference dbPostReference;
    private DatabaseReference dbCommentsReference;
    private ValueEventListener dbvPostListener;
    private String postKey;
    private CommentAdapter cmAdapter;

    private TextView textAuthorView;
    private TextView textGameName;
    private TextView textPlatform;
    private TextView textGamertag;
    private TextView textDescription;
    private TextView textHowManyPeople;
    private TextView textAvailability;
    private TextView textLocation;

    private EditText editTCommentField;
    private Button commentButton;
    private RecyclerView commentsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_info);

        // Get post key from intent to get post data, if not notify it notifies us that we
        // didn't pass the post key to the activity.
        postKey = getIntent().getStringExtra("post_key");
        if (postKey == null) {
            throw new IllegalArgumentException("Must pass post_key");
        }

        // Initialize Database
        dbPostReference = FirebaseDatabase.getInstance().getReference()
                .child("posts").child(postKey);
        dbCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child("post-comments").child(postKey);

        // Initialize Views
        textAuthorView = findViewById(R.id.post_author);
        textGameName = findViewById(R.id.post_game_name);
        textPlatform = findViewById(R.id.post_platform);
        textGamertag = findViewById(R.id.post_gamertag);
        textDescription = findViewById(R.id.post_description);
        textHowManyPeople = findViewById(R.id.post_how_many_people);
        textAvailability = findViewById(R.id.post_avail);
        textLocation = findViewById(R.id.post_locat);

        editTCommentField = findViewById(R.id.comment_input);
        commentButton = findViewById(R.id.button_post_comment);
        commentsRecycler = findViewById(R.id.recycler_comments);

        commentButton.setOnClickListener(this);
        commentsRecycler.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Post post = dataSnapshot.getValue(Post.class);

                textAuthorView.setText(post.author);
                textGameName.setText(post.gameName);
                textPlatform.setText(post.platform);
                textGamertag.setText(post.gamerTag);
                textDescription.setText(post.description);
                textHowManyPeople.setText(post.numOfPeople);
                textAvailability.setText(post.availability);
                textLocation.setText(post.location);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message and notify user
                Log.w("PostInfo", "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(PostInfo.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        dbPostReference.addValueEventListener(postListener);

        // Keep copy of post listener so we can remove it when app stops
        dbvPostListener = postListener;

        // Listen for comments
        cmAdapter = new CommentAdapter(this, dbCommentsReference);
        commentsRecycler.setAdapter(cmAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener since it is not needed anymore
        if (dbvPostListener != null) {
            dbPostReference.removeEventListener(dbvPostListener);
        }

        // Clean up comments listener to make way for more
        cmAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_post_comment) {
            postComment();
        }
    }

    private void postComment() {

        // Get the user ID of the user posting
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information from data
                        User user = dataSnapshot.getValue(User.class);
                        String authorName = user.username;

                        // Create a new comment object
                        String commentText = editTCommentField.getText().toString();
                        Comment comment = new Comment(uid, authorName, commentText);

                        // Push the comment, and it will appear in the list
                        dbCommentsReference.push().setValue(comment);

                        // Clears the field
                        editTCommentField.setText(null);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView authorView;
        public TextView bodyView;

        public CommentViewHolder(View itemView) {
            super(itemView);

            authorView = itemView.findViewById(R.id.comment_author);
            bodyView = itemView.findViewById(R.id.comment_body);
        }
    }

    private static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private List<String> mCommentIds = new ArrayList<>();
        private List<Comment> mComments = new ArrayList<>();

        public CommentAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;

            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d("PostInfo", "onChildAdded:" + dataSnapshot.getKey());

                    // A new comment has been added, add it to the comment list
                    Comment comment = dataSnapshot.getValue(Comment.class);

                    // Update RecyclerView to show the comment
                    mCommentIds.add(dataSnapshot.getKey());
                    mComments.add(comment);
                    notifyItemInserted(mComments.size() - 1);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d("PostInfo", "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    Comment newComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Replace with the new comment data
                        mComments.set(commentIndex, newComment);

                        // Update the RecyclerView to show the change
                        notifyItemChanged(commentIndex);
                    } else {
                        Log.w("PostInfo", "onChildChanged:unknown_child:" + commentKey);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d("PostInfo", "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it to prepare for the change in onChildChanged.
                    String commentKey = dataSnapshot.getKey();

                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Remove data from the list of comments
                        mCommentIds.remove(commentIndex);
                        mComments.remove(commentIndex);

                        // Update the RecyclerView to remove the comment
                        notifyItemRemoved(commentIndex);
                    } else {
                        Log.w("PostInfo", "onChildRemoved:unknown_child:" + commentKey);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d("PostInfo", "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it to the present DB location.
                    Comment movedComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();
                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("PostInfo", "postComments:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);

            // Store reference to a listener so it can be removed on app stop
            mChildEventListener = childEventListener;
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            Comment comment = mComments.get(position);
            holder.authorView.setText(comment.author);
            holder.bodyView.setText(comment.replyText);
        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

    }
}
