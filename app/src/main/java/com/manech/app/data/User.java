package com.manech.app.data;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(indices = {@Index(value = {"email"},unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String userName;
    private String password;
    private String email;
    private String urlPicture;

    public User(String userName, String password, String email, String urlPicture) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.urlPicture = urlPicture;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
