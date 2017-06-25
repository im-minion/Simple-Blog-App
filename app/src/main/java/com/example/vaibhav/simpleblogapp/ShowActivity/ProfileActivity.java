package com.example.vaibhav.simpleblogapp.ShowActivity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vaibhav.simpleblogapp.R;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    CircleImageView circleImageViewProfile;
    TextView textViewProfile;
    String uimage;
    String uname;
    Button buttonProfile;
    private DatabaseReference mDatabseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        bindViews();
        mDatabseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDatabseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("name")) {
                    uname = String.valueOf(dataSnapshot.child("name").getValue());
                    String temp = getString(R.string.log_in_as) + uname;
                    textViewProfile.setText(temp);
                }
                if (dataSnapshot.hasChild("image")) {
                    uimage = String.valueOf(dataSnapshot.child("image").getValue());
                    Log.d("fgfgfgfg", " " + uimage);
                    Picasso.with(getApplicationContext()).load(uimage).into(circleImageViewProfile);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent logOut = new Intent(ProfileActivity.this, LoginActivity.class);
                logOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logOut);
            }
        });
    }

    private void bindViews() {
        circleImageViewProfile = (CircleImageView) findViewById(R.id.ProfilecircleImageView);
        textViewProfile = (TextView) findViewById(R.id.profileTextView);
        buttonProfile = (Button) findViewById(R.id.profileLogOut);
    }
}
