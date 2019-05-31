package com.example.schat;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabItem;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.widget.LinearLayout;

import com.example.schat.Chat.ChatObject;
import com.example.schat.User.FindUserActivity;
import com.example.schat.Chat.ChatListAdapter;
import com.example.schat.User.UserObject;
import com.example.schat.Utils.SendNotification;
import com.example.schat.unimplemented.ContactsActivity;
import com.example.schat.unimplemented.ProfileActivity;
import com.example.schat.unimplemented.SettingsActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.util.ArrayList;

public class MainFeedActivity extends AppCompatActivity {

    private RecyclerView chatListView;
    private RecyclerView.Adapter chatListAdapter;
    private RecyclerView.LayoutManager chatListLayoutManager;

    ArrayList<ChatObject> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_feed);
        OneSignal.startInit(this).init();
        OneSignal.setSubscription(true);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("notificationKey").setValue(userId);
            }
        });
        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);


        Fresco.initialize(this);

        // contact search
        Button mFindUser = findViewById(R.id.find_user);

        mFindUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainFeedActivity.this, FindUserActivity.class));
            }
        });
        // profile

        // logout button
        Button mLogout = findViewById(R.id.logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneSignal.setSubscription(false);
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), PhoneLogin.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return;
            }
        });


        getPermissions();
        initializeRecyclerView();
        getUserChatList();
   }
    private void getUserChatList(){

        DatabaseReference userChatDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat");
        userChatDB.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren())
                    {
                        ChatObject mChat = new ChatObject(childSnapshot.getKey());
                        boolean  exists = false;
                        for (ChatObject chatIterator : chatList){
                            if (chatIterator.getChatId().equals(mChat.getChatId()))
                                exists = true;
                        }
                        if (exists)
                            continue;
                        chatList.add(mChat);
                        getChatData(mChat.getChatId());
                        //chatListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });



    }

    private void getChatData(String chatId) {
        DatabaseReference chatDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatId).child("info");
        chatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String chatId = "";
                    if(dataSnapshot.child("id").getValue() != null)
                    {
                        chatId = dataSnapshot.child("id").getValue().toString();
                    }
                    for(DataSnapshot userSnapShot : dataSnapshot.child("users").getChildren())
                    {
                        for(ChatObject chatIterator : chatList)
                        {
                            if(chatIterator.getChatId().equals(chatId))
                            {
                                UserObject mUser = new UserObject(userSnapShot.getKey());
                                chatIterator.addUserToArrayList(mUser);
                                getUserData(mUser);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserData(UserObject mUser) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user").child(mUser.getUid());
        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserObject mUser = new UserObject(dataSnapshot.getKey());

                if(dataSnapshot.child("notificationKey").getValue() != null)
                {
                    mUser.setNotificationKey(dataSnapshot.child("notificationKey").getValue().toString());
                }
                for(ChatObject mChat : chatList)
                {
                    for(UserObject userIterator : mChat.getUserObjectArrayList()){
                        if(userIterator.getUid().equals(mUser.getUid())){
                            userIterator.setNotificationKey((mUser.getNotificationKey()));

                            //This is also where we could grab user name or profile picture and other similar user data.
                        }
                    }
                }
                chatListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void initializeRecyclerView() {
        chatList = new ArrayList<>();
        chatListView = findViewById(R.id.chatList);
        chatListView.setNestedScrollingEnabled(false);
        chatListView.setHasFixedSize(false);
        chatListLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        chatListView.setLayoutManager(chatListLayoutManager);
        chatListAdapter = new ChatListAdapter(chatList);
        chatListView.setAdapter(chatListAdapter);
    }


    public void goToContacts(View view){
        Intent intent = new Intent (this, ContactsActivity.class);
        startActivity(intent);
    }
    public void goToProfile(View view){
        Intent intent = new Intent (this, ProfileActivity.class);
        startActivity(intent);
    }
    public void goToSettings(View view){
        Intent intent = new Intent (this, SettingsActivity.class);
        startActivity(intent);
    }
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
        }
    }
}
