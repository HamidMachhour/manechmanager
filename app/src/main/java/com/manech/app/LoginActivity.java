package com.manech.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.manech.app.data.User;
import com.manech.app.model.Injection;
import com.manech.app.model.UserViewModel;


public class LoginActivity extends AppCompatActivity{
    private UserViewModel userViewModel;

    @Override
    protected void onResume() {
        super.onResume();
        final long userId;
        if((userId=loadUserId())!=-1 ){
            startMainActivity(userId);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userViewModel = Injection.provideViewModelFactory(this).create(UserViewModel.class);
        userViewModel.getCurrentUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user!=null) {
                    saveUserId(user.getId());
                    startMainActivity(user.getId());
                }else{
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.wrong_auth), Toast.LENGTH_SHORT).show();
                }
            }
        });
        final EditText txtEmail= (EditText) findViewById(R.id.txtEmail);
        final EditText txtPassword= (EditText) findViewById(R.id.txtPassword);
        final TextView tvRegisterLink=(TextView) findViewById(R.id.tvRegisterLink);
        final Button btnLogin= findViewById(R.id.btnLogin);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get User Input
                final String email = txtEmail.getText().toString();
                final String password = txtPassword.getText().toString();
                userViewModel.authenticate(email,password);
            }
        });

        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
               startActivity(intent);

            }
        });
    }

    private long loadUserId() {
        SharedPreferences sharedPref =getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getLong(getString(R.string.saved_user_id),-1L);
    }

    private void startMainActivity(long userId) {
        final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(getString(R.string.saved_user_id), userId);
        // start main activity
        startActivity(intent);
        //start service
        intent.setComponent(new ComponentName("com.manech.app", "com.manech.app.service.ManechService"));
        getApplicationContext().startService(intent);
    }

    private void saveUserId(long id) {
        SharedPreferences sharedPref =getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sharedPref.edit().putLong(getString(R.string.saved_user_id), id).apply();
    }
}
