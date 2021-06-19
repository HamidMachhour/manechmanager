package com.manech.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    private final String TAG = this.getClass().getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userViewModel = Injection.provideViewModelFactory(this).create(UserViewModel.class);
        userViewModel.getCurrentUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user!=null) {
                    Log.d(TAG,user.toString());
                    final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userId", user.getId());
                    startActivity(intent);
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
}
