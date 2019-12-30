package com.push.notification.getter;

/**
 * Created by Himanshu Sharma on 09/07/19.
 */

public class Notifications {

    private String from, message, time;

    public Notifications(String from, String message, String time) {
        this.from = from;
        this.message = message;
        this.time = time;
    }

    public Notifications() {

    }

    public String getFrom() {
        return from;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }
}
