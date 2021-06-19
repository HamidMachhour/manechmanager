package com.manech.app.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.manech.app.data.User;
import com.manech.app.repository.UserRepository;

import java.util.concurrent.Executor;

public class UserViewModel extends ViewModel {
    private UserRepository userRepository;
    private  Executor executor;

    // DATA
    private MutableLiveData<User> currentUser = new MutableLiveData<User>();
    
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void authenticate(String email, String password) {
        executor.execute(() -> {
            currentUser.postValue(userRepository.userAuth(email, password));
        });
    }

    public void insert(User user) {
        executor.execute(() -> {
            userRepository.insert(user);
        });
    }




    public LiveData<User> getCurrentUser() {
        if(currentUser==null)
            currentUser = new MutableLiveData<User>();
        return currentUser;
    }

    public void userByEmail(String email) {
        executor.execute(() -> {
            currentUser.postValue(userRepository.getUserByEmail(email));
        });
    }

    public void fetchUser(long userId) {
        executor.execute(()->
                this.currentUser.postValue(userRepository.getUserById(userId))
        );
    }

    public void updateUser(long id, String userName, String imageUrl, String oldPassword, String password) {
        executor.execute(() -> {
            userRepository.update(id,userName,imageUrl,oldPassword,password);
        });
        fetchUser(id);
    }

    public void deleteUser(long userId) {

            executor.execute(() -> {
                userRepository.delete(userId);
            });
        fetchUser(userId);

    }
}
