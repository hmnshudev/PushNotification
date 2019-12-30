package com.push.notification.notification;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.push.notification.R;

import java.util.HashMap;
import java.util.Map;

public class SendActivity extends AppCompatActivity {

    TextView user_id_view;
    String mUserName;
    Button mSendBtn;
    private String mUserId;
    private String mCurrentId;
    private EditText mMessageView;
    private ProgressBar mMessageProgress;

    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        user_id_view = findViewById(R.id.user_name_view);
        mMessageView = findViewById(R.id.message_view);
        mSendBtn = findViewById(R.id.send_btn);
        mMessageProgress = findViewById(R.id.messageProgress);

        mFirestore = FirebaseFirestore.getInstance();
        mCurrentId = FirebaseAuth.getInstance().getUid();

        mUserId = getIntent().getStringExtra("user_id");
        mUserName = getIntent().getStringExtra("user_name");

        user_id_view.setText(getString(R.string.send, mUserName));

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = mMessageView.getText().toString();

                long aLong = System.currentTimeMillis();

                if (!TextUtils.isEmpty(message)) {

                    mMessageProgress.setVisibility(View.VISIBLE);

                    Map<String, Object> notificationMessage = new HashMap<>();
                    notificationMessage.put("message", message);
                    notificationMessage.put("from", mCurrentId);
                    notificationMessage.put("time", Long.toString(aLong));

                    mFirestore.collection("Users/" + mUserId + "/Notifications").add(notificationMessage).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            Toast.makeText(SendActivity.this, "Notification Sent.", Toast.LENGTH_LONG).show();
                            mMessageView.setText("");
                            mMessageProgress.setVisibility(View.INVISIBLE);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(SendActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                            mMessageProgress.setVisibility(View.INVISIBLE);

                        }
                    });

                }

            }
        });

    }
}
