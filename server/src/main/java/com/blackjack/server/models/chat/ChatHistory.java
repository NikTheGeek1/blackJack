package com.blackjack.server.models.chat;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class ChatHistory {

    LinkedList<ChatMessage> messages;

    public ChatHistory() {
        this.messages = new LinkedList<>();
    }

    public void addMessage(ChatMessage message) {
        this.messages.add(message);
    }

    public LinkedList<ChatMessage> getMessages() {
        return messages;
    }
}
