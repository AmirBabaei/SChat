package com.example.schat.Chat;

import java.util.ArrayList;

public class MessageObject
{
    String  messageId, senderId, message, time, CreatorName;
    ArrayList<String> mediaUrLList;

    public MessageObject(String messageId, String senderId, String message, ArrayList<String> mediaUrLList, String time)
    {
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.mediaUrLList = mediaUrLList;
        this.time = time;
    }
    public MessageObject(String messageId, String senderId, String message, ArrayList<String> mediaUrLList, String time, String CreatorName)
    {
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.mediaUrLList = mediaUrLList;
        this.time = time;
        this.CreatorName = CreatorName;
    }

    public String getMessageId() { return messageId; }
    public String getSenderId() { return senderId; }
    public String getSenderName() { return CreatorName; }
    public String getMessage() { return message; }
    public ArrayList<String> getMediaUrLList() { return mediaUrLList; }
    public String getTime(){return time;}
}