package com.manech.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.manech.app.view.mnode.NodeFragment;
import com.manech.app.view.mnode.NodesFragment;
import com.manech.app.view.user.UserFragment;


public class MainActivity extends AppCompatActivity {

    private long userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();
        userId = i.getLongExtra("userId", -1);

        // set toolbar
        Toolbar toolbar = findViewById(R.id.menu_bar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.app_name));
        replaceFragment(initNodesFragment(userId));
    }


    private NodesFragment initNodesFragment(long userId) {
        Bundle bundle = new Bundle(1);
        bundle.putLong("userId", userId);
        NodesFragment f = new NodesFragment();
        f.setArguments(bundle);
        return f;
    }
    private void replaceFragment (Fragment fragment){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);
        if (!fragmentPopped){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.main_frame_layout, fragment,backStateName);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }
}
