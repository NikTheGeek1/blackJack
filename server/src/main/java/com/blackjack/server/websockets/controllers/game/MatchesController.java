package com.blackjack.server.websockets.controllers.game;

import com.blackjack.server.models.match.Match;
import com.blackjack.server.urls.URLs;
import com.blackjack.server.websockets.managers.ActiveMatchesManager;
import com.blackjack.server.websockets.interfaces.ActiveMatchesChangeListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;

@Controller
public class MatchesController implements ActiveMatchesChangeListener {

    @Autowired
    private SimpMessagingTemplate webSocket;

    @Autowired
    private ActiveMatchesManager activeMatchesManager;

    @PostConstruct
    private void init() {
        activeMatchesManager.registerListener(this);
    }

    @PreDestroy
    private void destroy() {
        activeMatchesManager.removeListener(this);
    }

    @Override
    public void notifyActiveMatchChange () {
        HashMap<String, Match> activeMatches = activeMatchesManager.getAll();
        webSocket.convertAndSend("/topic/active", activeMatches);
    }

    @MessageMapping(URLs.ON_LIST_OF_MATCHES)
    public void getListOfMatches() {
        System.out.println("getting list of matches");
        webSocket.convertAndSend(URLs.REPLY_TO_LIST_OF_MATCHES, "List of matches");
    }

}
