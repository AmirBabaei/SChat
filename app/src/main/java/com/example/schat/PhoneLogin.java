 package com.example.schat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.schat.unimplemented.ContactsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

 public class PhoneLogin extends AppCompatActivity {
     EditText phoneNumber;
     EditText verificationCode;
     Button sendVerificationCode;
     private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
     String verificationId;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_phone_login);
         setup();
         userIsLoggedIn();
     }
     @Override
     public void onBackPressed() {
// empty so nothing happens
     }
     private void setup(){
         FirebaseApp.initializeApp(this);
         // so that the user doesnt have to continually login
         userIsLoggedIn();

         phoneNumber = findViewById(R.id.phoneNumber);
         verificationCode = findViewById(R.id.verificationCode);
         sendVerificationCode = findViewById(R.id.sendVerification);

         phoneNumber.setHint("Enter your phone number");
         verificationCode.setHint("Enter the verification code");
         sendVerificationCode.setText("Send Verification Code");
         sendVerificationCode.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v)
             {
                 if(verificationId != null)
                 {
                     verifyPhoneNumberWithCode();
                 }
                 else {
                     startPhoneNumberVerification();
                 }
                 Toast.makeText(PhoneLogin.this, "Sent verification code", Toast.LENGTH_SHORT).show();
             }

         }
         );
         callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
             @Override
             public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                 signInWithPhoneAuthCredential(phoneAuthCredential);
             }

             @Override
             public void onVerificationFailed(FirebaseException e) {

             }

             @Override
             public void onCodeSent(String s , PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                 super.onCodeSent(s , forceResendingToken);
                 verificationId =  s;
                 Toast.makeText(PhoneLogin.this, "Sent verification code", Toast.LENGTH_SHORT).show();
                 sendVerificationCode.setText("Verify Code");
             }
         };
     }
     private void verifyPhoneNumberWithCode()
     {
         PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verificationCode.getText().toString());
         signInWithPhoneAuthCredential(credential);
     }

     private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
         FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) { // validates phone number
                         if (task.isSuccessful())
                         {
                             final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                             if(user != null)
                             {

                                 final DatabaseReference userDB= FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());
                                 userDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                         if(!dataSnapshot.exists()){
                                             Map<String, Object> userMap = new HashMap<>();
                                             userMap.put("phone_number", user.getPhoneNumber());
                                             userMap.put("public_key", user.getUid());
                                             // data is sent off to firebase here
                                             userDB.updateChildren(userMap);
                                         }
                                         userIsLoggedIn();
                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError databaseError) {

                                     }
                                 });

                             }
                         }

                     }
                 });
     }

     private void userIsLoggedIn() {
         FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
         if(user !=null) // double check that a user is logged in
         {
             startActivity(new Intent(getApplicationContext(), MainFeedActivity.class));
             finish();
             return;
         }
     }
     private void startPhoneNumberVerification() {
         try {
             PhoneAuthProvider.getInstance().verifyPhoneNumber(
                     phoneNumber.getText().toString(),
                     60,
                     TimeUnit.SECONDS,
                     this,
                     callbacks);
         }
         catch(IllegalArgumentException e)
         {
             Toast.makeText(PhoneLogin.this, "Phone number is blank", Toast.LENGTH_SHORT).show();
         }
     }

     public void goToFeed(View view){
         Intent intent = new Intent (this, MainFeedActivity.class);
         startActivity(intent);
     }
     public void goToContacts(View view){
         Intent intent = new Intent (this, ContactsActivity.class);
         startActivity(intent);
     }

 }
