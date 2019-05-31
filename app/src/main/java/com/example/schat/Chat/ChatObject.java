package com.example.schat.Chat;


import com.example.schat.User.UserObject;

import java.io.Serializable;
        import java.util.ArrayList;

public class ChatObject implements Serializable {
    private String chatId;

    private ArrayList<UserObject> userObjectArrayList = new ArrayList<>();

    public ChatObject(String chatId){
        this.chatId = chatId;
    }

    public String getChatId() {
        return chatId;
    }
    public ArrayList<UserObject> getUserObjectArrayList() {
        return userObjectArrayList;
    }
    public int getSize()
    {
        return userObjectArrayList.size();
    }




    public void addUserToArrayList(UserObject mUser){
        userObjectArrayList.add(mUser);
    }
}