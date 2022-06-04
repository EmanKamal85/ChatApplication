package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URL;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {

    ImageView signUpImage;
    EditText signUpEmailEdittext, signUpPasswordEdittext, signUpUserNameEdittext;
    Button signUpButton;
    boolean imageControl = false;
    Uri imageUrl;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUpImage = findViewById(R.id.signUpImage);
        signUpEmailEdittext = findViewById(R.id.signUpEmailEdittext);
        signUpPasswordEdittext = findViewById(R.id.signUpPasswordEdittext);
        signUpUserNameEdittext = findViewById(R.id.signUpUserNameEdittext);
        signUpButton = findViewById(R.id.signUpButton);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        signUpImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = signUpEmailEdittext.getText().toString();
                String userPassword = signUpPasswordEdittext.getText().toString();
                String userName = signUpUserNameEdittext.getText().toString();

                if (!userEmail.equals(" ") && !userPassword.equals(" ") && !userName.equals(" ")){
                    signUp(userEmail, userPassword, userName);
                }else {
                    Toast.makeText(SignUpActivity.this, "Please enter your credentials", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void imageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            imageUrl = data.getData();
            Picasso.get().load(imageUrl).into(signUpImage);
            imageControl = true;
        }else{
            imageControl = false;
        }
    }

    public void signUp(String userEmail, String userPassword, String userName){
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            reference.child("Users").child(mAuth.getUid()).child("UserName").setValue(userName);

                            if (imageControl){
                                UUID randomID = UUID.randomUUID();
                                final String imageName = "images/" + randomID + ".jpg";
                                storageReference.child(imageName).putFile(imageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // String filePath = imageUrl.toString();



                                        storageReference.child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String filePath = uri.toString();
                                                reference.child("Users").child(mAuth.getUid()).child("UserImage").setValue(filePath)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(SignUpActivity.this, "Write to database successful", Toast.LENGTH_SHORT).show();

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(SignUpActivity.this, "Write to database fails", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }
                                        });



                                    }
                                });

                            }else {
                                reference.child("Users").child(mAuth.getUid()).child("UserImage").setValue("null");
                            }
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            intent.putExtra("userName", userName);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(SignUpActivity.this, "There's a problem signing up!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}