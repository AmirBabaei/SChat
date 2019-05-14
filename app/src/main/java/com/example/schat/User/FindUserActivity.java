package com.example.schat.User;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.widget.LinearLayout;

import com.example.schat.Utils.CountryToPhonePrefix;
import com.example.schat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindUserActivity extends AppCompatActivity {

    private RecyclerView userListView;
    private RecyclerView.Adapter userListAdapter;
    private RecyclerView.LayoutManager userListLayoutManager;

    ArrayList<UserObject> userList, contactList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        contactList = new ArrayList<>();
        userList = new ArrayList<>();

        /*
        chat is here

         */
        
        initializeRecyclerView();
        getContactList();
    }
    private void getContactList(){
        String ISOPrefix = getCountryISO();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while(phones.moveToNext()){
            String key = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            // normalize phone number string
            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");


            if(!String.valueOf(phone.charAt(0)).equals("+"))
                phone = ISOPrefix + phone;

            UserObject contact = new UserObject("", key, phone);
            contactList.add(contact);
            getUserDetails(contact);
        }
    }
    private void getUserDetails(UserObject contact) {
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = userDB.orderByChild("phone_number").equalTo(contact.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String  phone = "", public_key = "";
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren())
                    {
                        if(childSnapshot.child("phone_number").getValue()!=null)
                            phone = childSnapshot.child("phone_number").getValue().toString();
                        if(childSnapshot.child("public_key").getValue()!=null)
                            public_key = childSnapshot.child("public_key").getValue().toString();

                        // This display a user name instead of a phone number
                        // getKey is the firebase uid
                        UserObject user = new UserObject(childSnapshot.getKey(), public_key, phone);
                        if (public_key.equals(phone))
                            for(UserObject contactIterator : contactList){
                                if(contactIterator.getPhone().equals(user.getPhone())){
                                    user.setName(contactIterator.getName());
                                }
                            }
                        userList.add(user);
                        userListAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private String getCountryISO(){

        String iso = null;
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso() != null)
            if (!telephonyManager.getNetworkCountryIso().toString().equals(""))
                iso = telephonyManager.getNetworkCountryIso().toString();

        return CountryToPhonePrefix.getPhone(iso);

    }


    private void initializeRecyclerView() {
        userListView = findViewById(R.id.userList);
        userListView.setNestedScrollingEnabled(false);
        userListView.setHasFixedSize(false);
        userListLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        userListView.setLayoutManager(userListLayoutManager);
        userListAdapter = new UserListAdapter(userList);
        userListView.setAdapter(userListAdapter);
    }

}
