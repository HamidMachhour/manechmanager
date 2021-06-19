package com.manech.app.model;

import android.content.Context;

import androidx.lifecycle.ViewModelStoreOwner;

import com.manech.app.data.ManechDatabase;
import com.manech.app.helper.RequestQueueSingleton;
import com.manech.app.repository.ManechNodeRepository;
import com.manech.app.repository.UserRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Injection {

    private static ManechNodeRepository getManechNodeRepository(Context context) {
        return new ManechNodeRepository( ManechDatabase .getInstance(context).manechNodeDAO(), RequestQueueSingleton.getInstance(context).
                getRequestQueue());
    }

    private static UserRepository getUserRepository(Context context) {
        return new UserRepository(ManechDatabase.getInstance(context).userDAO(), RequestQueueSingleton.getInstance(context).
                getRequestQueue());
    }

    private static Executor getExecutor(){ return Executors.newSingleThreadExecutor(); }

    public static ViewModelFactory provideViewModelFactory(ViewModelStoreOwner viewModelStoreOwner) {
        ManechNodeRepository manechNodeRepository = getManechNodeRepository((Context)viewModelStoreOwner);
        UserRepository userRepository = getUserRepository((Context)viewModelStoreOwner);
        Executor executor = getExecutor();
        return new ViewModelFactory(manechNodeRepository, userRepository, executor, viewModelStoreOwner);
    }
}