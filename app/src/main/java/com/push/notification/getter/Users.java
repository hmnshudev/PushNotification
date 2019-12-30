package com.push.notification.getter;

/**
 * Created by Himanshu Sharma on 09/07/19.
 */

public class Users extends UserId {

    String name, image, status;

    public Users() {

    }

    public Users(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
