package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText loginEmailEdittext, loginPasswordEdittext;
    Button loginSignInButton, loginSignUpButton;
    TextView loginForgotTextview;

    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmailEdittext = findViewById(R.id.loginEmailEdittext);
        loginPasswordEdittext = findViewById(R.id.loginPasswordeditext);
        loginSignInButton = findViewById(R.id.logInSigninButton);
        loginSignUpButton = findViewById(R.id.loginSignupButton);
        loginForgotTextview = findViewById(R.id.loginForgotTextview);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        loginSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = loginEmailEdittext.getText().toString();
                String userPassword = loginPasswordEdittext.getText().toString();

                if (!userEmail.equals(" ") && !userPassword.equals(" ")){
                    signIn(userEmail, userPassword);
                }else{
                    Toast.makeText(LoginActivity.this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        loginForgotTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    public void signIn(String userEmail, String userPassword){
        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(LoginActivity.this, "Sign in complete", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(LoginActivity.this, "There is a problem", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
}