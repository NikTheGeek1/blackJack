package com.blackjack.server.websockets.controllers.chat;


import com.blackjack.server.models.chat.ChatHistory;
import com.blackjack.server.models.chat.ChatMessage;
import com.blackjack.server.models.game.GamePrep;
import com.blackjack.server.models.match.Match;
import com.blackjack.server.urls.URLs;
import com.blackjack.server.websockets.managers.ActiveMatchesManager;
import com.blackjack.server.websockets.managers.ChatMessagesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate webSocket;

    @Autowired
    ChatMessagesManager chatMessagesManager;

    @Autowired
    ActiveMatchesManager activeMatchesManager;

    @MessageMapping(URLs.GET_CHAT_HISTORY)
    public void getChatHistory(@DestinationVariable String gameName) {
        try {
            ChatHistory chatHistory = chatMessagesManager.getMatchHistory(gameName);
            webSocket.convertAndSend(URLs.UPDATE_CHAT_HISTORY(gameName), chatHistory.getMessages());
        } catch (NullPointerException e) {
            System.out.println("Chat history not created yet.");
        }
    }

    @MessageMapping(URLs.SEND_MESSAGE)
    public void sendMessage(@DestinationVariable String gameName, @Payload ChatMessage chatMessage) {
        chatMessagesManager.addMessageToMatchHistory(gameName, chatMessage);
        ChatHistory chatHistory = chatMessagesManager.getMatchHistory(gameName);
        webSocket.convertAndSend(URLs.UPDATE_CHAT_HISTORY(gameName), chatHistory.getMessages());
    }

    @MessageMapping(URLs.LEAVE_CHAT)
    public void leaveChat(@DestinationVariable String gameName) {
        chatMessagesManager.removeMatchHistory(gameName);
        Match match = activeMatchesManager.getMatch(gameName);
        if (match != null && match.getUsers() != null && match.getUsers().size() == 1) {// the leaver is the last player
            chatMessagesManager.removeMatchHistory(gameName);
        }
    }
}

