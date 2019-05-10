package com.example.schat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.Manifest;

public class MainFeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_feed);

        Button userLogout = findViewById(R.id.logout);
        Button mFindUser = findViewById(R.id.find_user);

        mFindUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainFeedActivity.this, FindUserActivity.class));
            }
        });

        getPermissions();
   }

    private void getPermissions() {
        requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
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
}
