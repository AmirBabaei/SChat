package com.example.schat.User;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.schat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    ArrayList<UserObject> userList;
    public UserListAdapter(ArrayList<UserObject> userList){
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        UserListViewHolder rcv = new UserListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserListViewHolder userListViewHolder, int position) {
        userListViewHolder.usrPublicKey.setText("User Public Key"/*userList.get(i).getPublicKey()*/);// uid for now
        userListViewHolder.usrName.setText(userList.get(position).getName());
        userListViewHolder.usrPhone.setText(userList.get(position).getPhone());

        userListViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createChat(userListViewHolder.getAdapterPosition());
            }
        });
    }
    private void createChat(int position)
    {
        String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

        HashMap newChatMap = new HashMap();
        newChatMap.put("id", key);
        newChatMap.put("users/" + FirebaseAuth.getInstance().getUid(), true);
        newChatMap.put("users/" + userList.get(position).getUid(), true);

        DatabaseReference chatInfoDb = FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("info");
        chatInfoDb.updateChildren(newChatMap);


        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user");

        userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
        // the user they click on
        userDb.child(userList.get(position).getUid()).child("chat").child(key).setValue(true);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class UserListViewHolder extends RecyclerView.ViewHolder{
        public TextView usrPublicKey, usrName, usrPhone;
        LinearLayout layout;
        public UserListViewHolder(View view){
            super(view);
            usrPublicKey = view.findViewById(R.id.publicKey);
            usrName = view.findViewById(R.id.name);
            usrPhone = view.findViewById(R.id.phone);
            layout = view.findViewById(R.id.itemUserLayout);
        }
    }
}
