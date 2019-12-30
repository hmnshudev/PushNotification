package com.push.notification.getter;

import android.support.annotation.NonNull;

/**
 * Created by Himanshu Sharma on 09/07/19.
 */

public class UserId {

    public String userId;

    public <T extends UserId> T withId(@NonNull final String id) {
        this.userId = id;
        return (T) this;
    }

}
