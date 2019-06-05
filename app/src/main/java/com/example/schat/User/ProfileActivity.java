package com.example.schat.User;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userDb = FirebaseDatabase.getInstance().getReference();

        nameView = findViewById(R.id.name);
        lastView = findViewById(R.id.lastname);
        emailView = findViewById(R.id.email);
        update = findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });
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
