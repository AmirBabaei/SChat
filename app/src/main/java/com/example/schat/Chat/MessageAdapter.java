package com.example.schat.Chat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.schat.R;
import com.google.firebase.database.FirebaseDatabase;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    ArrayList<MessageObject> messageList;
    public MessageAdapter(ArrayList<MessageObject> messageList)
    {
        this.messageList = messageList;
    }
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MessageViewHolder rcv = new MessageViewHolder(layoutView);
        return rcv;
    }
    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position)
    {
        holder.message.setText(messageList.get(position).getMessage());
        holder.sender.setText(messageList.get(position).getSenderId());

        if(messageList.get(holder.getAdapterPosition()).getMediaUrLList().isEmpty())
        {
            holder.viewMedia.setVisibility(View.GONE);
        }

        holder.viewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageViewer.Builder(v.getContext(), messageList.get(holder.getAdapterPosition()).getMediaUrLList())
                        .setStartPosition(0)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() { return messageList.size(); }

    class MessageViewHolder extends RecyclerView.ViewHolder
    {
        TextView  message, sender;
        Button viewMedia;
        LinearLayout layout;
        MessageViewHolder(View view)
        {
            super(view);
            layout = view.findViewById(R.id.sendLayout);
            message = view.findViewById(R.id.message);
            sender = view.findViewById(R.id.sender);

            viewMedia = view.findViewById(R.id.viewMedia);
        }
    }
}