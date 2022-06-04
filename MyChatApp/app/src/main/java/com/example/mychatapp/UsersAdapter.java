package com.example.mychatapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    List<String> usersList = new ArrayList<>();
    Context context;
    String userName;
    FirebaseDatabase database;
    DatabaseReference reference;


    public UsersAdapter(List<String> usersList, Context context, String userName) {
        this.usersList = usersList;
        this.context = context;
        this.userName = userName;
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_cardview, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        reference.child("Users").child(usersList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String otherName = snapshot.child("UserName").getValue().toString();
                String imageUri = snapshot.child("UserImage").getValue().toString();
                holder.userTextView.setText(otherName);
                if (imageUri.equals("null")){
                    holder.userImageView.setImageResource(R.drawable.ic_baseline_account_circle_24);
                }else {
                    File file = new File(imageUri);
                    Picasso.get().load(imageUri).into(holder.userImageView);
//                    Glide.with(context)
//                            .load(imageUri)
//                            .into(holder.userImageView);
                }

                holder.userCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, RealChatActivity.class);
                        intent.putExtra("userName", userName);
                        intent.putExtra("otherName", otherName);
                        intent.putExtra("imageUri", imageUri);
                        context.startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class UsersViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView userImageView;
        private TextView userTextView;
        private CardView userCardView;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);

            userImageView = itemView.findViewById(R.id.usersImageView);
            userTextView = itemView.findViewById(R.id.usersTextview);
            userCardView = itemView.findViewById(R.id.cardView);
        }
    }
}
