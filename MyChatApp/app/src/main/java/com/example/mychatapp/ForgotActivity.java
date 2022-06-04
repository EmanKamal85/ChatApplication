package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity {

    EditText forgotEmailEdittext;
    Button forgotResetButton;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        mAuth = FirebaseAuth.getInstance();

        forgotEmailEdittext = findViewById(R.id.forgetEmailEdittext);
        forgotResetButton = findViewById(R.id.forgotResetButton);

        forgotResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = forgotEmailEdittext.getText().toString();
                mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ForgotActivity.this, "Please check your email", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ForgotActivity.this, "Ther's a problem sending email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}