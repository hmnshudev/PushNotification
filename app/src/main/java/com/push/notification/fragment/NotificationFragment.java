package com.push.notification.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.push.notification.R;
import com.push.notification.adapter.NotificationsAdapter;
import com.push.notification.getter.Notifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends android.support.v4.app.Fragment {

    public RecyclerView mNotificationList;
    public FirebaseFirestore mFirestore;
    private NotificationsAdapter notificationsAdapter;
    private List<Notifications> mNotifList;


    public NotificationFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_notification, container, false);


        mNotifList = new ArrayList<>();

        mNotificationList = v.findViewById(R.id.notification_list);
        notificationsAdapter = new NotificationsAdapter(getContext(), mNotifList);


        mNotificationList.setHasFixedSize(true);
        mNotificationList.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mNotificationList.setAdapter(notificationsAdapter);

        mFirestore = FirebaseFirestore.getInstance();

        String current_user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        mFirestore.collection("Users").document(current_user_id).collection("Notifications").addSnapshotListener(Objects.requireNonNull(getActivity()), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    try {
                        Notifications notifications = doc.getDocument().toObject(Notifications.class);
                        mNotifList.add(notifications);

                        notificationsAdapter.notifyDataSetChanged();
                    } catch (Exception ef) {
                        Toast.makeText(getContext(), ef.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                }

            }
        });


        return v;
    }

}
