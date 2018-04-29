package com.msccs.hku.familycaregiver.Model;

import java.util.Calendar;

/**
 * Created by HoiKit on 07/01/2018.
 */

public class ChatMessage {

    private String messageText;
    private String senderUid;
    private String senderPhoneNo;
    private long messageSendOutTime;

    public ChatMessage(String messageText, String senderUid, String senderPhoneNo) {
        this.messageText = messageText;
        this.senderUid = senderUid;
        this.senderPhoneNo = senderPhoneNo;
        //initialize the time to current time
        this.messageSendOutTime = Calendar.getInstance().getTimeInMillis();
    }

    public String getSenderPhoneNo() {
        return senderPhoneNo;
    }

    public void setSenderPhoneNo(String senderPhoneNo) {
        this.senderPhoneNo = senderPhoneNo;
    }

    public ChatMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public long getMessageSendOutTime() {
        return messageSendOutTime;
    }

    public void setMessageSendOutTime(long messageSendOutTime) {
        this.messageSendOutTime = messageSendOutTime;
    }

}
