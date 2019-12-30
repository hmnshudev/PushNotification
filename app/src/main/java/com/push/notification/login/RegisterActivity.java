package com.push.notification.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.push.notification.MainActivity;
import com.push.notification.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final int STORAGE_REQUEST_CODE = 400;
    String[] storagePermission;
    Button mRegBtn;
    Button mLoginPageBtn;
    private CircleImageView mImageBtn;
    private EditText mEmailField;
    private EditText mNameField;
    private EditText mPasswordField;
    private Uri imageUri;

    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private ProgressBar mRegisterProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imageUri = null;

        mStorage = FirebaseStorage.getInstance().getReference().child("images");
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        mImageBtn = findViewById(R.id.register_image_btn);
        mEmailField = findViewById(R.id.register_email);
        mPasswordField = findViewById(R.id.register_password);
        mNameField = findViewById(R.id.register_name);
        mRegBtn = findViewById(R.id.register_btn);
        mLoginPageBtn = findViewById(R.id.register_login_btn);
        mRegisterProgressBar = findViewById(R.id.registerProgressBar);

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imageUri != null) {

                    mRegisterProgressBar.setVisibility(View.VISIBLE);

                    final String name = mNameField.getText().toString();
                    String email = mEmailField.getText().toString();
                    String password = mPasswordField.getText().toString();

                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    final String user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                                    StorageReference user_profile = mStorage.child(user_id + ".jpg");

                                    user_profile.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> uploadTask) {

                                            if (uploadTask.isSuccessful()) {

                                                String download_url = Objects.requireNonNull(uploadTask.getResult().getDownloadUrl()).toString();

                                                String token_id = FirebaseInstanceId.getInstance().getToken();

                                                Map<String, Object> userMap = new HashMap<>();
                                                userMap.put("name", name);
                                                userMap.put("image", download_url);
                                                assert token_id != null;
                                                userMap.put("token_id", token_id);
                                                userMap.put("status", "online");

                                                mFirestore.collection("Users").document(user_id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        mRegisterProgressBar.setVisibility(View.INVISIBLE);

                                                        sendToMain();

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                        Toast.makeText(RegisterActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                        mRegisterProgressBar.setVisibility(View.INVISIBLE);

                                                    }
                                                });


                                            } else {

                                                Toast.makeText(RegisterActivity.this, "Error : " + Objects.requireNonNull(uploadTask.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                                mRegisterProgressBar.setVisibility(View.INVISIBLE);

                                            }

                                        }
                                    });


                                } else {

                                    Toast.makeText(RegisterActivity.this, "Error : " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                    mRegisterProgressBar.setVisibility(View.INVISIBLE);

                                }

                            }
                        });

                    } else {
                        Toast.makeText(RegisterActivity.this, "Please Fill all Details", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(RegisterActivity.this, "Please Upload Profile Photo", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mLoginPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickGallery();
                }

            }
        });


    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private void pickGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void sendToMain() {

        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickGallery();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {

            imageUri = data.getData();
            mImageBtn.setImageURI(imageUri);

        } else {
            Toast.makeText(this, "Please select pic", Toast.LENGTH_SHORT).show();
        }

    }
}
