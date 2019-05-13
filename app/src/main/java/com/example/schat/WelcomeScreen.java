package com.example.schat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

public class WelcomeScreen extends AppCompatActivity {
    private RadioGroup welcomeScreenButtons = null;
    RadioButton phone = null;
    RadioButton email = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        FirebaseApp.initializeApp(this);
        setup();

    }
    private void setup()
    {

        welcomeScreenButtons = findViewById(R.id.welcomeScreenButtons);
        phone = findViewById(R.id.phoneAuthenticate);
        email = findViewById(R.id.emailAuthenticate);
        phone.setText("Use a phone number");
        email.setText("Use an email address");
        welcomeScreenButtons.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(phone.isChecked())
                {
                    Toast.makeText(WelcomeScreen.this, "Phone Selected", Toast.LENGTH_SHORT).show();
                    // transition to next screen
                    Intent next_screen = new Intent(getApplicationContext(), PhoneLogin.class);
                    startActivity(next_screen);
                }
                else {
                    Toast.makeText(WelcomeScreen.this, "Email selected", Toast.LENGTH_SHORT).show();
                    // transition to next screen
                    //Intent next_screen = new Intent(getApplicationContext(), MainActivity.class);
                    //startActivity(next_screen);
                }


            }
        });
    }


}
