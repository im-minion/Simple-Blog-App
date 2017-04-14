package com.example.vaibhav.simpleblogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {


    private EditText mLoginEmailField;
    private EditText mLoginPasswordField;
    private Button mLoginButton;
    private Button mNewAccount;

    private ProgressDialog mProgressbar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth.AuthStateListener mAuthListenere;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        mProgressbar = new ProgressDialog(this);
        mLoginEmailField = (EditText) findViewById(R.id.loginemailfield);
        mLoginPasswordField = (EditText) findViewById(R.id.loginpasswordfield);
        mLoginButton = (Button) findViewById(R.id.loginbtn);
        mNewAccount = (Button) findViewById(R.id.newaccount);

        mNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });


        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });

    }

    private void checkLogin() {

        String email = mLoginEmailField.getText().toString().trim();
        String password = mLoginPasswordField.getText().toString().trim();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "uname:" + email + " passwd:" + password, Toast.LENGTH_LONG).show();

            mProgressbar.setMessage("Checking LOGIN.....");
            mProgressbar.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mProgressbar.dismiss();
                        checkUserExist();

                    } else {
                        mProgressbar.dismiss();
                        Toast.makeText(LoginActivity.this, "TASK FAILED", Toast.LENGTH_LONG).show();
                        Log.w("LoginThing---", "signInWithEmail:failed", task.getException());
                    }
                }
            });

        }

    }

    private void checkUserExist() {

        final String user_id = mAuth.getCurrentUser().getUid();
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(user_id)) {

                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                } else {

                    Intent setupIntent = new Intent(LoginActivity.this, SetupActivity.class);
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
