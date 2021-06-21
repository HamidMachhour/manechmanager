package com.manech.app.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.manech.app.repository.ManechNodeRepository;
import com.manech.app.repository.UserRepository;

import java.util.concurrent.Executor;

public class ViewModelFactory implements ViewModelProvider.Factory  {

    private final ManechNodeRepository manechNodeRepository;
    private final UserRepository userRepository;
    private final Executor executor;
    private final Context context;
    private  final ViewModelStoreOwner viewModelStoreOwner;
    public ViewModelFactory(ManechNodeRepository manechNodeRepository, UserRepository userRepository, Executor executor, Context context) {
        this.manechNodeRepository = manechNodeRepository;
        this.userRepository = userRepository;
        this.executor = executor;
        this.context = context;
        this.viewModelStoreOwner=new ViewModelStoreOwner() {
            @NonNull
            @Override
            public ViewModelStore getViewModelStore() {
                return new ViewModelStore();
            }
        };
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MNodeViewModel.class)) {
            //return (T) new MNodeViewModel(manechNodeRepository, executor);
            final MNodeViewModel t =  new ViewModelProvider(viewModelStoreOwner)
                    .get(MNodeViewModel.class);
            t.setExecutor(executor);
            t.setManechNodeRepository(manechNodeRepository);
            return (T) t;
        }
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            //return (T) new UserViewModel(userRepository, executor);
            final UserViewModel t =  new ViewModelProvider(viewModelStoreOwner)
                    .get(UserViewModel.class);
            t.setExecutor(executor);
            t.setUserRepository(userRepository);
            return (T) t;
        }


        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}