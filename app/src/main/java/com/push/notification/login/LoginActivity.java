package com.push.notification.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.push.notification.MainActivity;
import com.push.notification.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    Button mLoginBtn;
    Button mRegPageBtn;
    private EditText mEmailField;
    private EditText mPasswordField;
    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            sendToMain();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mEmailField = findViewById(R.id.login_email);
        mPasswordField = findViewById(R.id.login_password);
        mLoginBtn = findViewById(R.id.login_btn);
        mRegPageBtn = findViewById(R.id.login_register_btn);

        mProgressBar = findViewById(R.id.loginProgress);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        mRegPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent regIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(regIntent);

            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

                    mProgressBar.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {


                                String token_id = FirebaseInstanceId.getInstance().getToken();
                                Log.d("Refreshed Token ", token_id);
                                String current_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                                Map<String, Object> tokenMap = new HashMap<>();
                                assert token_id != null;
                                tokenMap.put("token_id", token_id);

                                mFirestore.collection("Users").document(current_id).update(tokenMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        sendToMain();
                                        mProgressBar.setVisibility(View.INVISIBLE);

                                    }
                                });

                            } else {

                                Toast.makeText(LoginActivity.this, "Error : " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                mProgressBar.setVisibility(View.INVISIBLE);

                            }

                        }
                    });

                }

            }
        });


    }

    private void sendToMain() {

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();

    }
}
