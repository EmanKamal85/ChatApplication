package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RealChatActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    EditText chatMessageEdittext;
    FloatingActionButton chatFab;
    RecyclerView chatRv;
    TextView name;
    ImageView backArrow,imageViewChat;
    ImageButton imageButton;

    String userName;
    String otherName;
    String chatImage;
    String message;
    List<Message> messagesList;
    MessageAdapter adapter;
    Map<String, Object> messageMap = new HashMap<>();
    Uri imageUri;
    String key;
    boolean imageControl =false;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_chat);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setElevation(0);
        View view = getSupportActionBar().getCustomView();



        name = view.findViewById(R.id.name);
        backArrow = view.findViewById(R.id.backArrow);
        imageViewChat = view.findViewById(R.id.imageViewChat);
        chatMessageEdittext = findViewById(R.id.chatMessageEdittext);
        imageButton = findViewById(R.id.imageButton);
        chatFab = findViewById(R.id.fab);
        chatRv = findViewById(R.id.chatRv);
        chatRv.setLayoutManager(new LinearLayoutManager(this));
        messagesList = new ArrayList<>();


        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        otherName = intent.getStringExtra("otherName");
        chatImage = intent.getStringExtra("imageUri");
        name.setText(otherName);
        //Picasso.get().load(chatImage).into(imageViewChat);
        Glide.with(this)
                .load(chatImage)
                .into(imageViewChat);



        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RealChatActivity.this, "You have clicked title", Toast.LENGTH_LONG).show();
            }
        });
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RealChatActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        chatFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = chatMessageEdittext.getText().toString();
                chatMessageEdittext.setText(" ");
                Message friendlyMessage = new Message(userName, message, null);
                key = reference.child("Messages").child("UserName").child("OtherName").push().getKey();

                messageMap.put("message", message);
                messageMap.put("from", userName);
                reference.child("Messages").child(userName).child(otherName).child(key).setValue(friendlyMessage)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            reference.child("Messages").child(otherName).child(userName).child(key).setValue(friendlyMessage);
                        }
                    }
                });

            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        getMessages();
        adapter = new MessageAdapter(messagesList, userName, this);
        chatRv.setAdapter(adapter);


    }


    public void imageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    public void getMessages(){
        reference.child("Messages").child(userName).child(otherName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message customMessage = snapshot.getValue(Message.class);
                messagesList.add(customMessage);
                adapter.notifyDataSetChanged();
                //chatRv.scrollToPosition(messagesList.size()-1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            imageControl = true;
            uploadPhoto();
            //Picasso.get().load(imageUri).into();
        }else {
            imageControl = false;
        }
    }

    public void uploadPhoto(){

        UUID randomID = UUID.randomUUID();
        final String imageName = "messageImages/" + randomID + ".jpg";
        storageReference.child(imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(RealChatActivity.this, "Image is successfully uploaded to storage", Toast.LENGTH_SHORT).show();
                Task<Uri> uriTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                while ((!uriTask.isComplete()));

                Uri downloadUri = uriTask.getResult();
                Message friendlyMessage = new Message(userName,null, downloadUri.toString());
                reference.child("Messages").child(userName).child(otherName).push().setValue(friendlyMessage)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    reference.child("Messages").child(otherName).child(userName).push().setValue(friendlyMessage);
                                }

                            }
                        });
//                storageReference.child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        String filePath = uri.toString();
//                        key = reference.child("Messages").child("UserName").child("OtherName").push().getKey();
//                        Message friendlyMessage = new Message(userName, null, filePath);
//                        reference.child("Messages").child(userName).child(otherName).child(key).setValue(friendlyMessage)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()){
//                                            reference.child("Messages").child(otherName).child(userName).child(key).setValue(friendlyMessage);
//                                        }
//
//                                    }
//                                });
//
//                    }
//                });

            }
        });

    }
}