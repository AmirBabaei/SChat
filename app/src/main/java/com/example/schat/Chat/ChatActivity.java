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

import com.example.schat.Chat.MediaAdapter;
import com.example.schat.Chat.MessageAdapter;
import com.example.schat.Chat.MessageObject;
import com.example.schat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView messageListView, mediaListView;
    private RecyclerView.Adapter messageAdapter, mediaAdapter;
    private RecyclerView.LayoutManager messageListLayoutManager, mediaListLayourManager;

    ArrayList<MessageObject> messageList;
    String chatID;
    DatabaseReference chatMessagesDb;

    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriList = new ArrayList<>();
    ArrayList<String> mediaIdList = new ArrayList<>();
    EditText mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatID = getIntent().getExtras().getString("chatID");
        chatMessagesDb = FirebaseDatabase.getInstance().getReference().child("chat").child(chatID);
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
        chatMessagesDb.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.exists())
                {
                    String  text = "", creatorID = "";

                    ArrayList<String> mediaUrlList = new ArrayList<>();
                    if(dataSnapshot.child("text").getValue() != null)
                        text = dataSnapshot.child("text").getValue().toString();
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

                    MessageObject mMessage = new MessageObject(dataSnapshot.getKey(), creatorID, text, mediaUrlList);
                    messageList.add(mMessage);
                    messageListLayoutManager.scrollToPosition(messageList.size()-1);//scrolls down to latest message
                    messageAdapter.notifyDataSetChanged();
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
    int mediaPosition = 0;
    private void sendMessage()
    {
        mMessage =  findViewById(R.id.messageInput);
            String messageId = chatMessagesDb.push().getKey();
            final DatabaseReference newMessageDb = chatMessagesDb.child(messageId);
            final Map newMessageMap = new HashMap<>();

            newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());
            if(!mMessage.getText().toString().isEmpty())
            {
                newMessageMap.put("text", mMessage.getText().toString());
            }


            if(!mediaUriList.isEmpty()){
                for(String mediaUri : mediaUriList){
                    final String mediaId = newMessageDb.child("media").push().getKey();
                    mediaIdList.add(mediaId);
                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("chat").child(messageId).child(mediaId);

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
    }

    private void updateDatabaseWithNewMessage(DatabaseReference dbReference, Map newMessageMap)
    {
        dbReference.updateChildren(newMessageMap);
        mMessage.setText(null);
        mediaIdList.clear();
        mediaUriList.clear();
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
