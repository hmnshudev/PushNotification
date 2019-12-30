package com.push.notification.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.push.notification.MainActivity;
import com.push.notification.R;
import com.push.notification.login.LoginActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends android.support.v4.app.Fragment {

    Button mLogoutBtn, mDeleteBtn;
    String user_image;
    private FirebaseAuth mAuth;
    private CircleImageView mProfileImage;
    private TextView mProfileName;
    private FirebaseFirestore mFirestore;
    private String mUserId;
    private ProgressBar progressBar;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        mUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        mLogoutBtn = view.findViewById(R.id.logout_btn);
        mDeleteBtn = view.findViewById(R.id.delete_btn);
        mDeleteBtn.setVisibility(View.GONE);
        mProfileImage = view.findViewById(R.id.profile_image);
        mProfileName = view.findViewById(R.id.profile_name);
        progressBar = view.findViewById(R.id.logoutProgress);

        mFirestore.collection("Users").document(mUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String user_name = documentSnapshot.getString("name");
                user_image = documentSnapshot.getString("image");

                mProfileName.setText(user_name);

                RequestOptions placeholderOption = new RequestOptions();
                placeholderOption.placeholder(R.mipmap.default_image);

                Glide.with(container.getContext()).setDefaultRequestOptions(placeholderOption).load(user_image).into(mProfileImage);

            }
        });


        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);

                Map<String, Object> tokenMapRemove = new HashMap<>();
                tokenMapRemove.put("token_id", FieldValue.delete());

                mFirestore.collection("Users").document(mUserId).update(tokenMapRemove).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mAuth.signOut();
                        Intent loginIntent = new Intent(container.getContext(), LoginActivity.class);
                        startActivity(loginIntent);

                    }
                });

            }
        });

        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                mFirestore.collection("Users").document(mUserId).collection("Notifications").document().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFirestore.collection("Users").document(mUserId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseStorage.getInstance().getReferenceFromUrl(user_image).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mAuth.getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //mAuth.signOut();
                                                Intent loginIntent = new Intent(container.getContext(), LoginActivity.class);
                                                startActivity(loginIntent);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        return view;

    }

}
