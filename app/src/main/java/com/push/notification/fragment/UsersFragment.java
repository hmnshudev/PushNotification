package com.push.notification.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.push.notification.R;
import com.push.notification.adapter.UsersRecyclerAdapter;
import com.push.notification.getter.Users;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends android.support.v4.app.Fragment {

    RecyclerView mUsersListView;

    private List<Users> usersList;
    private UsersRecyclerAdapter usersRecyclerAdapter;

    private FirebaseFirestore mFirestore;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        mFirestore = FirebaseFirestore.getInstance();

        mUsersListView = view.findViewById(R.id.users_list_view);

        usersList = new ArrayList<>();
        usersRecyclerAdapter = new UsersRecyclerAdapter(container.getContext(), usersList);
        setData();
        mUsersListView.setHasFixedSize(true);
        mUsersListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mUsersListView.setAdapter(usersRecyclerAdapter);


        return view;
    }

    private void setData() {

        usersList.clear();

        mFirestore.collection("Users").addSnapshotListener(Objects.requireNonNull(getActivity()), new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.MODIFIED) {

                        String user_id = doc.getDocument().getId();
                        int index = doc.getOldIndex();

                        Users users = doc.getDocument().toObject(Users.class).withId(user_id);
                        usersList.set(index, users);
                        usersRecyclerAdapter.notifyDataSetChanged();
                    }

                }

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        usersList.clear();

        mFirestore.collection("Users").addSnapshotListener(Objects.requireNonNull(getActivity()), new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String user_id = doc.getDocument().getId();

                        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Users users = doc.getDocument().toObject(Users.class).withId(user_id);
                       /* if (!currentUser.equals(user_id)) {
                            usersList.add(users);
                        }*/
                        usersList.add(users);
                        usersRecyclerAdapter.notifyDataSetChanged();
                    }

                }

            }
        });
    }
}
