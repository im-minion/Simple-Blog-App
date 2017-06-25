package com.example.vaibhav.simpleblogapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class ProfileExists extends AppCompatActivity {
    private TextView tv;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_exists);
        tv = (TextView) findViewById(R.id.textView);
        iv = (ImageView) findViewById(R.id.imageView);
        String uname = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        tv.setText("Logged in as " + uname);
        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() == null){
            Picasso.with(ProfileExists.this).load("http://www.gpp-kavkaz.ru/images/no_avatar.jpg?crc=238954602").into(iv);
        }else {
            Picasso.with(ProfileExists.this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(iv);
        }
    }
}
