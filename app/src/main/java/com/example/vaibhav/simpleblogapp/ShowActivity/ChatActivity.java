package com.example.vaibhav.simpleblogapp.ShowActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private static final int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<ChatMessage> adapter;
    ListView listOfMessages;
    private static final int RC_SIGN_IN = 200;
    private static final String PATH_TOS = "";
    String messgaeLastText;
    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean previouslyStarted = prefs.getBoolean("prev", false);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        listOfMessages = (ListView) findViewById(R.id.list_of_messages);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(ChatActivity.this, LoginActivity.class));
        } else {
            displayChatMessages();
            lastMessage();

        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(getApplicationContext(), "Not logged in!", Toast.LENGTH_SHORT);
                    onStart();
                } else {
                    input = (EditText) findViewById(R.id.input);
                    // Read the input field and push a new instance
                    // of ChatMessage to the Firebase database
                    String message = input.getText().toString();
                    if (message.isEmpty()) {
                        input.setError("You can't post an empty Message. !!");
                    } else {
                        if (FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() == null) {
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .push()
                                    .setValue(new ChatMessage(input.getText().toString(),
                                                    FirebaseAuth.getInstance()
                                                            .getCurrentUser()
                                                            .getDisplayName(),
                                                    "http://www.gpp-kavkaz.ru/images/no_avatar.jpg?crc=238954602")
                                            //TODO: here there is a chnce of error....chk im not able to do that
                                            //FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString() this is giving error
                                            //problem is of android version
                                    );
                            Log.d("abcdabcd", String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));
                            // Clear the input
                        } else {
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .push()
                                    .setValue(new ChatMessage(input.getText().toString(),
                                                    FirebaseAuth.getInstance()
                                                            .getCurrentUser()
                                                            .getDisplayName(),
                                                    FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString())
                                            //TODO: here there is a chnce of error....chk im not able to do that
                                            //FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString() this is giving error
                                            //problem is of android version
                                    );
                            Log.d("abcdabcd", String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));
                            // Clear the input
                        }

                    }
                    input.setText("");
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
//            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
//                    .setTosUrl(PATH_TOS)
//                    .setIsSmartLockEnabled(true)
//                    .setProviders(Arrays.asList(
//                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
//                    ))
//                    .build(), RC_SIGN_IN);
        }
    }

    private void displayChatMessages() {
        adapter = new FirebaseListAdapter<ChatMessage>(ChatActivity.this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);
                CircleImageView proileUrl = (CircleImageView) v.findViewById(R.id.profile_image);
                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                //sets image to chat
                Picasso.with(getApplicationContext())
                        .load(model.getProfileUrl())
                        .into(proileUrl);

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };

        listOfMessages.setAdapter(adapter);
    }

    private void lastMessage() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // change this to your databae ref
        final DatabaseReference messages = database
                .getReference()
                .child("Chats"); // change this to your databae ref

        messages.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    // access last message
                    DataSnapshot messageSnapShot = dataSnapshot.getChildren().iterator().next();
                    messgaeLastText = (String) messageSnapShot.child("messageText").getValue();
                    Log.v("Value", messgaeLastText);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
