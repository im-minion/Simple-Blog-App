package com.example.vaibhav.simpleblogapp.ShowActivity;

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

import com.example.vaibhav.simpleblogapp.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private EditText mLoginEmailField;
    private EditText mLoginPasswordField;
    private Button mLoginButton;
    private Button mNewAccount;
    private ProgressDialog mProgressbar;
    private DatabaseReference mDatabaseUsers;
    private SignInButton signInButton;
    private FirebaseAuth.AuthStateListener mAuthListenere;

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindViews();

        mAuth = FirebaseAuth.getInstance();

        mNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });
        signInButton.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //*****updateUI(currentUser);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }
//1455972557797661
    //d36f1ea27ff477805f7d382e88b1ed84
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mAuth = FirebaseAuth.getInstance();
                            Toast.makeText(LoginActivity.this, "Welcome..!", Toast.LENGTH_SHORT).show();
                            checkUserExist();

                            //***updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            //****updateUI(null);
                        }

                        // ...
                    }
                });
    }


    private void bindViews() {
        mAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        mProgressbar = new ProgressDialog(this);

        mLoginEmailField = (EditText) findViewById(R.id.loginemailfield);
        mLoginPasswordField = (EditText) findViewById(R.id.loginpasswordfield);
        mLoginButton = (Button) findViewById(R.id.loginbtn);
        mNewAccount = (Button) findViewById(R.id.newaccount);
        signInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.google_sign_in_button) {
            signIn();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
