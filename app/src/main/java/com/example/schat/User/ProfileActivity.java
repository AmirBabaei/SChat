package com.example.schat.User;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.schat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    Button update;
    TextView nameView, lastView, emailView;
    DatabaseReference userDb;
    String name, last, email;
    ImageView profimg;
    Uri ImageUri;

    final static int gallPick =1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userDb = FirebaseDatabase.getInstance().getReference();

        nameView = findViewById(R.id.name);
        lastView = findViewById(R.id.lastname);
        emailView = findViewById(R.id.email);
        profimg =  findViewById(R.id.imageView);
        update = findViewById(R.id.update);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });

        profimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galerryIntent = new Intent();
                galerryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galerryIntent.setType("image/*");
                startActivityForResult(galerryIntent,gallPick);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == gallPick && resultCode == RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            profimg.setImageURI(ImageUri);
        }

    }

    void updateUserInfo()
    {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
        {
            if(nameView.getText().toString() != null) {
                userDb.child("user").child(user.getUid()).child("name").setValue(nameView.getText().toString());
            }
            if(lastView.getText().toString() != null) {
                userDb.child("user").child(user.getUid()).child("lastName").setValue(lastView.getText().toString());
            }
            if(emailView.getText().toString() != null) {
                userDb.child("user").child(user.getUid()).child("email").setValue(emailView.getText().toString());
            }

        }
        finish();
    }


}
