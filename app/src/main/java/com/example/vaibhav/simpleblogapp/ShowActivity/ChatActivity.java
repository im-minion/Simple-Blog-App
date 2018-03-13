package com.example.vaibhav.simpleblogapp.ShowActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaibhav.simpleblogapp.Models.ChatMessage;
import com.example.vaibhav.simpleblogapp.R;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private static final int SIGN_IN_REQUEST_CODE = 1;
    //    ListView listOfMessages;
    private static final int RC_SIGN_IN = 200;
    private static final String PATH_TOS = "";
    String messgaeLastText;
    EditText input;
    RecyclerView chatRecView;
    DatabaseReference dbChatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean previouslyStarted = prefs.getBoolean("prev", false);
        FloatingActionButton fab = findViewById(R.id.fab);
//        listOfMessages = findViewById(R.id.list_of_messages);

        chatRecView = findViewById(R.id.list_of_messages);
        dbChatRef = FirebaseDatabase.getInstance().getReference("/chat");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(false);
        chatRecView.setHasFixedSize(true);
        chatRecView.setLayoutManager(layoutManager);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(ChatActivity.this, LoginActivity.class));
        } else {
//            displayChatMessages();
//            lastMessage();

        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabClick();
            }
        });
    }

    private void fabClick() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(getApplicationContext(), "Not logged in!", Toast.LENGTH_SHORT).show();
            onStart();
        } else {
            input = findViewById(R.id.input);
            // Read the input field and push a new instance
            // of ChatMessage to the Firebase database
            String message = input.getText().toString();
            if (message.isEmpty()) {
                input.setError("You can't post an empty Message. !!");
            } else {
                if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() == null) {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("chat")
                            .push()
                            .setValue(new ChatMessage(
                                    input.getText().toString(),
                                    FirebaseAuth.getInstance()
                                            .getCurrentUser()
                                            .getEmail(),
                                    "null")
                            );
                    Log.d("abcdabcd", String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));
                    // Clear the input
                } else {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("chat")
                            .push()
                            .setValue(new ChatMessage(input.getText().toString(),
                                    FirebaseAuth.getInstance()
                                            .getCurrentUser()
                                            .getEmail(),
                                    FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString())
                            );
                }
            }
            input.setText("");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
        final FirebaseRecyclerAdapter<ChatMessage, ChatViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ChatMessage, ChatViewHolder>(
                        ChatMessage.class,
                        R.layout.message_row,
                        ChatViewHolder.class,
                        dbChatRef) {
                    @Override
                    protected void populateViewHolder(ChatViewHolder viewHolder, ChatMessage model, int position) {
                        final String chatKey = getRef(position).getKey();

                        viewHolder.setMessageText(model.getMessageText());
                        viewHolder.setMessageTime(model.getMessageTime());
                        viewHolder.setUserName(model.getMessageUser());
//                            viewHolder.setUserProfileImage(model.getProfileUrl());
                    }
                };
        chatRecView.setAdapter(firebaseRecyclerAdapter);
//        }
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView messageTime;
        TextView messageText;
        CircleImageView userProfileImage;

        public ChatViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.message_user);
            messageTime = (TextView) itemView.findViewById(R.id.message_time);
            messageText = (TextView) itemView.findViewById(R.id.message_text);
            userProfileImage = (CircleImageView) itemView.findViewById(R.id.profile_image);
        }

        public void setUserName(String usr) {
            userName.setText(usr);
        }

        public void setMessageTime(long time) {
            messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                    time));
        }

        public void setMessageText(String message) {
            messageText.setText(message);
        }

//        public void setUserProfileImage(String profile_url) {
//            Picasso.with(mctx)
//                    .load(profile_url)
//                    .into(userProfileImage);
//
//        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    private void displayChatMessages() {
        FirebaseListAdapter<ChatMessage> adapter = new FirebaseListAdapter<ChatMessage>(ChatActivity.this, ChatMessage.class,
                R.layout.message_row, FirebaseDatabase.getInstance().getReference("/chat")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                TextView messageText = v.findViewById(R.id.message_text);
                TextView messageUser = v.findViewById(R.id.message_user);
                TextView messageTime = v.findViewById(R.id.message_time);
                CircleImageView proileUrl = v.findViewById(R.id.profile_image);
                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                //sets image to chat
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (!Objects.equals(model.getProfileUrl(), "null")) {
                        Picasso.with(getApplicationContext())
                                .load(model.getProfileUrl())
                                .into(proileUrl);
                    } else {
                        proileUrl.setImageResource(R.drawable.ic_user_white);
                    }
                } else {
                    proileUrl.setImageResource(R.drawable.ic_user_white);

                }

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };

        //listOfMessages.setAdapter(adapter);
    }

    private void lastMessage() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // change this to your databae ref
        final DatabaseReference messages = database
                .getReference()
                .child("chat"); // change this to your databae ref

        messages.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    // access last message
                    DataSnapshot messageSnapShot = dataSnapshot.getChildren().iterator().next();
                    messgaeLastText = (String) messageSnapShot.child("messageText").getValue();
//                    Log.v("Value", messgaeLastText);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
