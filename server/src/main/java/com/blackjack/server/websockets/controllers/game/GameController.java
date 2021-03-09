package com.blackjack.server.websockets.controllers.game;

import com.blackjack.server.models.game.GamePrep;
import com.blackjack.server.models.match.Match;
import com.blackjack.server.urls.URLs;
import com.blackjack.server.websockets.managers.ActiveMatchesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

    @Autowired
    private SimpMessagingTemplate webSocket;

    @Autowired
    private ActiveMatchesManager activeMatchesManager;

    @MessageMapping(URLs.ENTER_GAME)
    public void enterGame(@DestinationVariable String gameName) {
        Match match = activeMatchesManager.getMatch(gameName);
        GamePrep.calibrateGame(match);
        webSocket.convertAndSend(URLs.UPDATE_GAME(gameName), match);
    }

    @MessageMapping(URLs.LEAVE_GAME)
    public void leaveGame(@DestinationVariable String gameName) {
        Match match = activeMatchesManager.getMatch(gameName);
        GamePrep.calibrateGame(match);
        webSocket.convertAndSend(URLs.UPDATE_GAME(gameName), match);
    }

    @MessageMapping(URLs.START_HUMANS_GAME)
    public void startGame(@DestinationVariable String gameName) {
        Match match = activeMatchesManager.getMatch(gameName);
        GamePrep.startGame(match);
        webSocket.convertAndSend(URLs.UPDATE_GAME(gameName), match);
    }
}
