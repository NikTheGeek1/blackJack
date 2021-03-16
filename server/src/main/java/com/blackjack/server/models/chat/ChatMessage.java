package com.blackjack.server.models.chat;

public class ChatMessage {

    public final String senderName;
    public final String senderEmail;
    public final String message;

    public ChatMessage(String senderName, String senderEmail, String message) {
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.message = message;
    }
}
