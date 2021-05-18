package com.example.chatapp.Chat;

import java.util.ArrayList;

public class MessageObject {

    String messageId,
            senderId,
            message,senderName;

    ArrayList<String> mediaUrlList;

    public MessageObject(String messageId, String senderId, String message, String senderName,ArrayList<String> mediaUrlList){
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.senderName=senderName;
        this.mediaUrlList=mediaUrlList;

    }

    public String getMessageId() {return messageId; }

    public ArrayList<String> getMediaUrlist() {
        return mediaUrlList;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getMessage() {return message; }

    public String getSenderId() {return senderId; }
}
