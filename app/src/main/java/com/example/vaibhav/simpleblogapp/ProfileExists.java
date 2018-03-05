package com.example.vaibhav.simpleblogapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vaibhav.simpleblogapp.ShowActivity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class ProfileExists extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_exists);
        TextView tv = (TextView) findViewById(R.id.textView);
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        String uname = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        tv.setText("Logged in as " + uname);
        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() == null) {
            Picasso.with(ProfileExists.this).load("http://www.gpp-kavkaz.ru/images/no_avatar.jpg?crc=238954602").into(iv);
        } else {
            Picasso.with(ProfileExists.this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(iv);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProfileExists.this, MainActivity.class));
    }
}
