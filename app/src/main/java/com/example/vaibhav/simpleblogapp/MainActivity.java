package com.example.vaibhav.simpleblogapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mBlogList;

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;//'

    private DatabaseReference mDatabaseUsers;

    private FirebaseAuth.AuthStateListener mAuthListener;//'


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {


                if (firebaseAuth.getInstance().getCurrentUser() == null) {

                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                } else {
                    Intent setupi = new Intent(MainActivity.this, PostActivity.class);
                    setupi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupi);
                }
            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        mDatabase.keepSynced(true);


        mBlogList = (RecyclerView) findViewById(R.id.blog_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mBlogList.setHasFixedSize(true);

        mBlogList.setLayoutManager(layoutManager);

        checkUserExist();


    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);


        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);//important thing!!!for sign out!!!

        FirebaseRecyclerAdapter<Blog, BloViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BloViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BloViewHolder.class,
                mDatabase) {
            @Override
            protected void populateViewHolder(BloViewHolder viewHolder, Blog model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDESCRIPTION());
                viewHolder.setImage(getApplicationContext(), model.getIMAGE());

            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);

    }

    private void checkUserExist() {

//        final String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (user != null) {
            final String user_id = user.getUid();
            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(user_id)) {
                        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }


    public static class BloViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public BloViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title) {

            TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }

        public void setDesc(String DESCRIPTION) {

            TextView post_description = (TextView) mView.findViewById(R.id.post_text);
            post_description.setText(DESCRIPTION);
        }

        public void setImage(Context ctx, String IMAGE) {

            ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(IMAGE).into(post_image);

        }

    }


    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_add) {

            startActivity(new Intent(MainActivity.this, PostActivity.class));
        }
        if (item.getItemId() == R.id.action_logout) {
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void logout() {

        mAuth.signOut();
    }
}