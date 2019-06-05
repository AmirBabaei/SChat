package com.example.schat.Chat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.schat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
        View layoutView;
        if( viewType == 0)
        {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, null, false);
        }
        else
        {
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, null, false);
        }
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MessageViewHolder rcv = new MessageViewHolder(layoutView);
        return rcv;
    }
    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position)
    {
        if(holder.getItemViewType() == 0)
        {
            holder.message.setText(messageList.get(position).getMessage());


            holder.sender.setText(messageList.get(position).getSenderId());
            holder.time.setText(messageList.get(position).getTime());

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
        else
        {
            holder.Rmessage.setText(messageList.get(position).getMessage());
            holder.Rsender.setText(messageList.get(position).getSenderId());
            holder.Rtime.setText(messageList.get(position).getTime());

            if(messageList.get(holder.getAdapterPosition()).getMediaUrLList().isEmpty())
            {
                holder.RviewMedia.setVisibility(View.GONE);
            }

            holder.RviewMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ImageViewer.Builder(v.getContext(), messageList.get(holder.getAdapterPosition()).getMediaUrLList())
                            .setStartPosition(0)
                            .show();
                }
            });
        }
    }
    @Override
    public int getItemViewType(int position)
    {
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messageList.get(position).getSenderId()))
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    @Override
    public int getItemCount() { return messageList.size(); }

    class MessageViewHolder extends RecyclerView.ViewHolder
    {
        TextView  message, sender, time, Rtime, Rmessage, Rsender;
        Button viewMedia, RviewMedia;
        LinearLayout layout, Rlayout;
        MessageViewHolder(View view)
        {
            super(view);
            layout = view.findViewById(R.id.sendLayout);
            message = view.findViewById(R.id.message);
            sender = view.findViewById(R.id.sender);
            time = view.findViewById(R.id.messageTime);


            viewMedia = view.findViewById(R.id.viewMedia);

            Rlayout = view.findViewById(R.id.sendLayout);
            Rmessage = view.findViewById(R.id.Rmessage);
            Rsender = view.findViewById(R.id.Rsender);
            RviewMedia = view.findViewById(R.id.RviewMedia);
            Rtime = view.findViewById(R.id.Rmessagetime);
        }
    }
}