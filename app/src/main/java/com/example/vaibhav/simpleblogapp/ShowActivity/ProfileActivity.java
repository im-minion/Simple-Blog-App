package com.example.vaibhav.simpleblogapp.ShowActivity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vaibhav.simpleblogapp.R;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    CircleImageView circleImageViewProfile;
    TextView textViewProfile;
    String username;
    Uri userImageUrl;
    Button buttonProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        bindViews();
        String temp = getString(R.string.log_in_as) + username;
        textViewProfile.setText(temp);
        if (userImageUrl != null) {
            Picasso.with(getApplicationContext()).load(userImageUrl).into(circleImageViewProfile);
        } else {
            circleImageViewProfile.setImageResource(R.drawable.profileicon9);
        }
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
        username = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userImageUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();

    }
}
