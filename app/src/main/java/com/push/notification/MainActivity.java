package com.push.notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.push.notification.adapter.PagerViewAdapter;
import com.push.notification.login.LoginActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public PagerViewAdapter mPagerViewAdapter;
    private TextView mProfileLabel;
    private TextView mUsersLabel;
    private TextView mNotificationLabel;
    private ViewPager mMainPager;
    private FirebaseAuth mAuth;
    private String user_id;
    private String user_id_set;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            sendToLogin();

        } else {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            user_id_set = user_id;

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("status", "online");
            firebaseFirestore.collection("Users").document(user_id).update(userMap);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        setStatus();

    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    public void setStatus() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        try {
            user_id = user_id_set;

            long aLong = System.currentTimeMillis();

            String nTimeStamp = Long.toString(aLong);
            final String nDate = "dd/MM/yy";
            String nTime = DateUtils.formatDateTime(MainActivity.this, Long.valueOf(nTimeStamp), DateUtils.FORMAT_SHOW_TIME);

            Calendar get = Calendar.getInstance();
            get.setTimeInMillis(Long.valueOf(nTimeStamp));
            Calendar now = Calendar.getInstance();

            if (now.get(Calendar.DATE) == get.get(Calendar.DATE)) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("status", nTime);
                firebaseFirestore.collection("Users").document(user_id).update(userMap);
                //holder.mTime.setText(MainActivity.this.getString(R.string.time, nTime));
            } else if (now.get(Calendar.DATE) - get.get(Calendar.DATE) == 1) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("status", R.string.yesterday);
                firebaseFirestore.collection("Users").document(user_id).update(userMap);
                //holder.mTime.setText(R.string.yesterday);
            } else {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("status", DateFormat.format(nDate, get).toString());
                firebaseFirestore.collection("Users").document(user_id).update(userMap);
                //holder.mTime.setText(MainActivity.this.getString(R.string.date, DateFormat.format(nDate, get).toString()));
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mProfileLabel = findViewById(R.id.profileLabel);
        mUsersLabel = findViewById(R.id.usersLabel);
        mNotificationLabel = findViewById(R.id.notificationsLabel);

        mMainPager = findViewById(R.id.mainPager);
        mMainPager.setOffscreenPageLimit(2);

        mPagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
        mMainPager.setAdapter(mPagerViewAdapter);

        mProfileLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mMainPager.setCurrentItem(0);

            }
        });

        mUsersLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mMainPager.setCurrentItem(1);

            }
        });

        mNotificationLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mMainPager.setCurrentItem(2);

            }
        });

        mMainPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                changeTabs(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void changeTabs(int position) {

        if (position == 0) {

            mProfileLabel.setTextColor(getColor(R.color.textTabBright));
            mProfileLabel.setTextSize(22);

            mUsersLabel.setTextColor(getColor(R.color.textTabLight));
            mUsersLabel.setTextSize(16);

            mNotificationLabel.setTextColor(getColor(R.color.textTabLight));
            mNotificationLabel.setTextSize(16);

        }

        if (position == 1) {

            mProfileLabel.setTextColor(getColor(R.color.textTabLight));
            mProfileLabel.setTextSize(16);

            mUsersLabel.setTextColor(getColor(R.color.textTabBright));
            mUsersLabel.setTextSize(22);

            mNotificationLabel.setTextColor(getColor(R.color.textTabLight));
            mNotificationLabel.setTextSize(16);

        }

        if (position == 2) {

            mProfileLabel.setTextColor(getColor(R.color.textTabLight));
            mProfileLabel.setTextSize(16);

            mUsersLabel.setTextColor(getColor(R.color.textTabLight));
            mUsersLabel.setTextSize(16);

            mNotificationLabel.setTextColor(getColor(R.color.textTabBright));
            mNotificationLabel.setTextSize(22);

        }

    }
}
