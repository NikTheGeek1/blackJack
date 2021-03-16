package com.blackjack.server.websockets.managers;

import com.blackjack.server.models.chat.ChatHistory;
import com.blackjack.server.models.chat.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;


@Component
public class ChatMessagesManager {

    private HashMap<String, ChatHistory> matchHistoryMessages;

    public ChatMessagesManager() {
        this.matchHistoryMessages = new HashMap<>();
    }

    public ChatHistory getMatchHistory(String matchName) {
        return matchHistoryMessages.get(matchName);
    }

    private void addMatchHistory(String matchName) {
        matchHistoryMessages.put(matchName, new ChatHistory());
    }

    public void removeMatchHistory(String matchName) {
        matchHistoryMessages.remove(matchName);
    }

    public void addMessageToMatchHistory(String matchName, ChatMessage message) {
        if (!matchHistoryMessages.containsKey(matchName)) {
            addMatchHistory(matchName);
        }
        ChatHistory chatHistory = matchHistoryMessages.get(matchName);
        chatHistory.addMessage(message);
    }
}
