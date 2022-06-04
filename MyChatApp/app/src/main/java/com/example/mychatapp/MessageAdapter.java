package com.example.mychatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    List<Message> messagesList;
    String userName;
    boolean status;
    int send;
    int receive;
    Context mContext;

    public MessageAdapter(List<Message> messagesList, String userName, Context mContext) {
        this.messagesList = messagesList;
        this.userName = userName;
        this.mContext = mContext;
        status = false;
        send = 1;
        receive = 2;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        //view = LayoutInflater.from(mContext).inflate(R.layout.test_card, parent, false);
        if (viewType == send){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_send, parent, false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_receive, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.nameTextView.setText(messagesList.get(position).getFrom());
        holder.messageTextView.setText(messagesList.get(position).getMessage());
        if (messagesList.get(position).getImageUrl() != null){
            //Picasso.get().load(messagesList.get(position).getImageUrl()).into(holder.photoImageView);
            Glide.with(mContext)
                    .load(messagesList.get(position).getImageUrl())
                    .into(holder.photoImageView);
        }

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messagesList.get(position).getFrom().equals(userName)){
            status = true;
            return send;
        }else {
            status = false;
            return receive;
        }
    }

     class MessageViewHolder extends RecyclerView.ViewHolder{

        ImageView photoImageView;
        TextView nameTextView, messageTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
//         photoImageView = itemView.findViewById(R.id.photoImageView);
//         nameTextView = itemView.findViewById(R.id.nameTextView);
//         messageTextView = itemView.findViewById(R.id.messageTextView);

            if(status){
                photoImageView = itemView.findViewById(R.id.sendPhotoImageView);
                nameTextView = itemView.findViewById(R.id.sendNameTextView);
                messageTextView = itemView.findViewById(R.id.sendTextView);
            }else {
                photoImageView = itemView.findViewById(R.id.receivePhotoImageView);
                nameTextView = itemView.findViewById(R.id.receiveNameTextView);
                messageTextView = itemView.findViewById(R.id.receiveTextView);
            }
        }
    }
}
