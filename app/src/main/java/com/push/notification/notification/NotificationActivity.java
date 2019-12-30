package com.push.notification.notification;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.push.notification.R;

public class NotificationActivity extends AppCompatActivity {

    public TextView mNotifData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        String dataMessage = getIntent().getStringExtra("message");
        String dataFrom = getIntent().getStringExtra("from_user_id");

        mNotifData = findViewById(R.id.notif_text);

        mNotifData.setText(getString(R.string.notification, dataFrom, dataMessage));


    }
}
