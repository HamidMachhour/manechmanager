package com.manech.app;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.manech.app.data.User;
import com.manech.app.helper.StringValidation;
import com.manech.app.model.Injection;
import com.manech.app.model.UserViewModel;


public class RegisterActivity extends AppCompatActivity{
    private UserViewModel userViewModel;

    private final StringValidation stringValidation=new StringValidation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final EditText txtPassword=findViewById(R.id.txtPassword);
        final EditText txtConfirm=findViewById(R.id.txtConfirm);
        final EditText txtEmail=findViewById(R.id.txtEmail);
        final Button btnRegister=findViewById(R.id.btnRegister);


        userViewModel = Injection.provideViewModelFactory(this).create(UserViewModel.class);
        userViewModel.getCurrentUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user!=null)  {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.email_already_exists), Toast.LENGTH_SHORT).show();

                }else{
                    final String email= txtEmail.getText().toString();
                    final String password= txtPassword.getText().toString();
                    try {
                        userViewModel.insert(new User("", password, email, ""));
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.user_created_success), Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                    }catch(Exception e){
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.user_create_faild), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email= txtEmail.getText().toString();
                final String password= txtPassword.getText().toString();
                final String confirm=txtConfirm.getText().toString();
                if(!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))
                {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.email_warning), Toast.LENGTH_LONG).show();
                }
                else if(!stringValidation.isValidPassword(password))
                {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.password_warning), Toast.LENGTH_LONG).show();
                }
                else if(!confirm.equals(password))
                {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.password_confirm_warning), Toast.LENGTH_LONG).show();
                }
                else{
                    userViewModel.userByEmail(email);
                }


            }
        });
    }

}
