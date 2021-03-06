package com.example.schat.Chat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.schat.Encryption.Encryption;
import com.example.schat.R;
import com.example.schat.User.UserObject;
import com.example.schat.Utils.SendNotification;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView messageListView, mediaListView;
    private RecyclerView.Adapter messageAdapter, mediaAdapter;
    private RecyclerView.LayoutManager messageListLayoutManager, mediaListLayourManager;

    ArrayList<MessageObject> messageList;
    DatabaseReference chatMessagesDb;

    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriList = new ArrayList<>();
    ArrayList<String> mediaIdList = new ArrayList<>();
    EditText mMessage;
    int mediaPosition = 0;

    Map userMap;

    ChatObject mChatObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatObject = (ChatObject) getIntent().getSerializableExtra("chatObject");

        chatMessagesDb = FirebaseDatabase.getInstance().getReference().child("chat").child(mChatObject.getChatId()).child("messages");
        Button addMedia = findViewById(R.id.addMedia);
        Button send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sendMessage();
            }
        });
        addMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openGallery();
            }


        });
        initializeMessage();
        initializeMedia();
        getChatMessages();
    }

    private void getChatMessages()
    {
        userMap = new HashMap<>();
        DatabaseReference chatDB = FirebaseDatabase.getInstance().getReference().child("user");
        chatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Log.d("USERMAP", "got datasnap");
                    String userID = "", userName ="";
                    for(DataSnapshot userSnapShot : dataSnapshot.getChildren())
                    {
                        Log.d("USERMAP", "got userSnap");
                        if(userSnapShot.getKey() != null)
                        {
                            userID = userSnapShot.getKey().toString();
                            Log.d("USERMAP", "UserID: " + userSnapShot.getKey().toString());
                        }
                        if(userSnapShot.child("name").getValue() != null)
                        {
                            userName = userSnapShot.child("name").getValue().toString();
                            Log.d("USERMAP", "UserName: " + userSnapShot.child("name").getValue().toString());
                        }
                        if(!(userID.equals("") && userName.equals(""))) {
                            Log.d("USERMAP","INTSERTING ID: " + userID +" UserName:" + userName);
                            userMap.put(userID, userName);
                            userID = "";
                            userName = "";
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        chatMessagesDb.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                Log.d("USERMAP","THE MAP: " + Arrays.asList(userMap).toString());
                if(dataSnapshot.exists())
                {
                    String  text = "", creatorID = "", messageTime="";

                    ArrayList<String> mediaUrlList = new ArrayList<>();
                    // Decryption here
                    try
                    {
                        if(dataSnapshot.child("text").getValue() != null)
                            text = Encryption.decrypt(dataSnapshot.child("text").getValue().toString());
                        if(dataSnapshot.child("time").getValue() != null)
                            messageTime = Encryption.decrypt(dataSnapshot.child("time").getValue().toString());
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    if(dataSnapshot.child("creator").getValue() != null)
                        creatorID = dataSnapshot.child("creator").getValue().toString();
                    if(dataSnapshot.child("media").getChildrenCount() > 0)
                    {
                        for(DataSnapshot mediaSnapShot : dataSnapshot.child("media").getChildren())
                        {
                            mediaUrlList.add(mediaSnapShot.getValue().toString());
                            Log.d("GOT IMAGE", mediaSnapShot.getValue().toString());
                        }
                    }


                    MessageObject mMessage = new MessageObject(dataSnapshot.getKey(), creatorID, text, mediaUrlList, messageTime, userMap.get(creatorID).toString());
                    messageList.add(mMessage);
                    messageAdapter.notifyDataSetChanged();
                    messageListLayoutManager.scrollToPosition(messageList.size()-1);//scrolls down to latest message

                }
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
            {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });
    }
    private void sendMessage()
    {
        mMessage =  findViewById(R.id.messageInput);
        String messageId = chatMessagesDb.push().getKey();
        final DatabaseReference newMessageDb = chatMessagesDb.child(messageId);
        final Map newMessageMap = new HashMap<>();

        newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());
        // Encryption here
        String encrypted_message = null, encrypted_time = null;
        if(!mMessage.getText().toString().isEmpty())
        {
            try {

                String strDateFormat = "MMM d  hh:mm a";
                DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
                String formattedDate= dateFormat.format(Calendar.getInstance().getTime());
                encrypted_message = Encryption.encrypt(mMessage.getText().toString());
                encrypted_time    = Encryption.encrypt(formattedDate);
                if(encrypted_message != null)
                    newMessageMap.put("text", /*mMessage.getText().toString()*/encrypted_message);
                if(encrypted_time != null)
                    newMessageMap.put("time", /*mMessage.getText().toString()*/encrypted_time);

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }


        if(!mediaUriList.isEmpty()){
            for(String mediaUri : mediaUriList){
                final String mediaId = newMessageDb.child("media").push().getKey();
                mediaIdList.add(mediaId);
                final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("chat").child(mChatObject.getChatId()).child(messageId).child(mediaId);

                UploadTask uploadtask = filePath.putFile(Uri.parse(mediaUri));

                uploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newMessageMap.put("/media/" + mediaIdList.get(mediaPosition) + "/", uri.toString());
                                    Log.d("IMAGE INSERTED", mediaId);
                                    mediaPosition++;
                                    if(mediaPosition == mediaUriList.size())
                                    {
                                        updateDatabaseWithNewMessage(newMessageDb, newMessageMap);
                                    }
                                }
                            });
                        }
                    });
                }
            }
            else
            {
                if(!mMessage.getText().toString().isEmpty())
                {
                    updateDatabaseWithNewMessage(newMessageDb, newMessageMap);
                }
            }

        mMessage.setText(null);
        mediaAdapter.notifyDataSetChanged();
    }

    private void updateDatabaseWithNewMessage(DatabaseReference dbReference, Map newMessageMap)
    {
        dbReference.updateChildren(newMessageMap);
        mMessage.setText(null);
        mediaIdList.clear();
        mediaUriList.clear();
        mediaPosition=0;

        String message;

        if(newMessageMap.get("text") != null)
        {
            message = newMessageMap.get("text").toString();
            try
            {
                message = Encryption.decrypt(message);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            message = "Sent Media";
        }

        for(UserObject user : mChatObject.getUserObjectArrayList()){
            if(!user.getUid().equals(FirebaseAuth.getInstance().getUid())){
                Log.d(user.getUid().toString(), "updateDatabaseWithNewMessage: Notification should be send compare: " + FirebaseAuth.getInstance().getUid().toString());
                new SendNotification(message, "New Message", user.getNotificationKey());
            }
        }
        mediaAdapter.notifyDataSetChanged();
    }

    private void initializeMessage() {
        messageList = new ArrayList<>();
        messageListView = findViewById(R.id.messageList);
        messageListView.setNestedScrollingEnabled(false);
        messageListView.setHasFixedSize(false);
        messageListLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        messageListView.setLayoutManager(messageListLayoutManager);
        messageAdapter = new MessageAdapter(messageList);
        messageListView.setAdapter(messageAdapter);
    }
    private void initializeMedia() {
        mediaUriList = new ArrayList<>();
        mediaListView = findViewById(R.id.mediaList);
        mediaListView.setNestedScrollingEnabled(false);
        mediaListView.setHasFixedSize(false);
        mediaListLayourManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.HORIZONTAL, false);
        mediaListView.setLayoutManager(mediaListLayourManager);
        mediaAdapter = new MediaAdapter(getApplicationContext(), mediaUriList);
        mediaListView.setAdapter(mediaAdapter);
    }
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select Picture(s)"), PICK_IMAGE_INTENT );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == PICK_IMAGE_INTENT){
                if(data.getClipData() == null) {
                    mediaUriList.add(data.getData().toString());
                }
                else
                {
                    for(int i = 0; i < data.getClipData().getItemCount(); i++){
                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }

                mediaAdapter.notifyDataSetChanged();
            }
        }
    }
}
