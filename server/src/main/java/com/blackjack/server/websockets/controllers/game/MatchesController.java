package com.blackjack.server.websockets.controllers.game;

import com.blackjack.server.models.match.Match;
import com.blackjack.server.urls.URLs;
import com.blackjack.server.websockets.managers.ActiveMatchesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;

@EnableScheduling
@Controller
public class MatchesController{

    @Autowired
    private SimpMessagingTemplate webSocket;

    @Autowired
    private ActiveMatchesManager activeMatchesManager;


    @MessageMapping(URLs.ON_LIST_OF_MATCHES)
    public void getListOfMatches() {
        System.out.println("getting list of matches");
        HashMap<String, Match> publicMatches = activeMatchesManager.getPublicAndAvailableMatches();
        webSocket.convertAndSend(URLs.REPLY_TO_LIST_OF_MATCHES, publicMatches);
    }

    @Scheduled(fixedRate = 5000)
    public void getListOfMatchesPeriodically() {
        activeMatchesManager.updateMatchesDuration();
        activeMatchesManager.removeEmptyMatches();
        HashMap<String, Match> publicMatches = activeMatchesManager.getPublicAndAvailableMatches();
        webSocket.convertAndSend(URLs.REPLY_TO_LIST_OF_MATCHES, publicMatches);
    }

}
