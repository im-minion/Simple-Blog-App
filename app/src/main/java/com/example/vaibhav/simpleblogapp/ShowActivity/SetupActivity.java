package com.example.vaibhav.simpleblogapp.ShowActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.vaibhav.simpleblogapp.FCMthings.SharedPrefManager;
import com.example.vaibhav.simpleblogapp.FCMthings.MyfirebaseInstanceServices;
import com.example.vaibhav.simpleblogapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupActivity extends AppCompatActivity {

    private ImageButton mSetupImage;
    private EditText mNameField;
    private Button mFinishBtn;
    private Uri mImageUri = null;
    private DatabaseReference mDatabseUsers;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private static final int GALLARY_REQUEST = 1;
    private ProgressDialog mProgress;
    private BroadcastReceiver broadcastReceiver;
    private String tokenString = null;

    public SetupActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        registerReceiver(broadcastReceiver, new IntentFilter(MyfirebaseInstanceServices.TOKEN_BROADCAST));
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                tokenString = SharedPrefManager.getInstance(SetupActivity.this).getToken();
            }
        };
        tokenString = SharedPrefManager.getInstance(SetupActivity.this).getToken();
//        Log.d("received", " " + tokenString);
        bindViews();
        onClickMethods();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLARY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                mSetupImage.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("setupError", error + "");
            }
        }
    }

    private void bindViews() {
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");
        mDatabseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mSetupImage = (ImageButton) findViewById(R.id.setupImagebtn);
        mNameField = (EditText) findViewById(R.id.setupName);
        mFinishBtn = (Button) findViewById(R.id.finishbtn);
        mProgress = new ProgressDialog(this);
    }

    private void onClickMethods() {

        mSetupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLARY_REQUEST);
            }
        });
        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSetupAccount();
            }
        });
    }

    private void startSetupAccount() {
        final String name = mNameField.getText().toString().trim();
        final String user_id = mAuth.getCurrentUser().getUid();
        if (!TextUtils.isEmpty(name) && mImageUri != null) {
            mProgress.setMessage("Saving Profile.....");
            mProgress.show();
            StorageReference filepath = mStorageRef.child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String downloadUri = taskSnapshot.getDownloadUrl().toString();
                            mDatabseUsers.child(user_id).child("name").setValue(name);
                            mDatabseUsers.child(user_id).child("image").setValue(downloadUri);
                            mDatabseUsers.child(user_id).child("token").setValue(tokenString);
                            Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mProgress.dismiss();
                            Toast.makeText(SetupActivity.this, "Profile Saved Successfully :)", Toast.LENGTH_LONG).show();
                            startActivity(mainIntent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(SetupActivity.this, "FAILED!!", Toast.LENGTH_LONG).show();
                            mProgress.dismiss();
                        }
                    });

        }
    }

}
