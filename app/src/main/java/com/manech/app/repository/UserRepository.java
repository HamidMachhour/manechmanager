package com.manech.app.repository;

import com.android.volley.RequestQueue;
import com.manech.app.dao.UserDAO;
import com.manech.app.data.User;

public class UserRepository {
    private UserDAO userDAO;
    private RequestQueue requestQueue;

    public UserRepository(UserDAO userDAO, RequestQueue requestQueue) {
        this.userDAO = userDAO;
        this.requestQueue = requestQueue;
    }

    public void insert(User user) {
        userDAO.insert(user);
    }

    public void update(User user) {
        userDAO.update(user);
    }

    public User userAuth(String email, String password) {
        return userDAO.userAuth(email, password);
    }

    public User getUserById(long userId) {
        return userDAO.userById(userId);
    }

    public User getUserByEmail(String email) {
        return userDAO.userByEmail(email);
    }

    public void update(long id, String userName, String imageUrl, String oldPassword, String password) {
        if (oldPassword != null)
            userDAO.update(id, userName, imageUrl, oldPassword, password);
        else
            userDAO.update(id, userName, imageUrl);

    }

    public void delete(long userId) {
        userDAO.deleteNodesOf(userId);
        userDAO.delete(userId);
    }
}
