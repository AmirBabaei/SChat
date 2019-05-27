package com.example.schat.User;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

        userListViewHolder.Add.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userList.get(userListViewHolder.getAdapterPosition()).setSelected(isChecked);
            }
        });


    }


    @Override
    public int getItemCount() {
        return userList.size();
    }


     class UserListViewHolder extends RecyclerView.ViewHolder{
        TextView usrPublicKey, usrName, usrPhone;
        LinearLayout layout;
        CheckBox Add;
        UserListViewHolder(View view){
            super(view);
            usrPublicKey = view.findViewById(R.id.publicKey);
            usrName = view.findViewById(R.id.name);
            usrPhone = view.findViewById(R.id.phone);
            Add = view.findViewById(R.id.add);
            layout = view.findViewById(R.id.itemUserLayout);
        }
    }
}
