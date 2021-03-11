package com.blackjack.server.websockets.controllers.game;

import com.blackjack.server.models.game.Bet;
import com.blackjack.server.models.game.GamePrep;
import com.blackjack.server.models.match.Match;
import com.blackjack.server.urls.URLs;
import com.blackjack.server.websockets.managers.ActiveMatchesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Timer;
import java.util.TimerTask;

@Controller
public class GameController {

    @Autowired
    private SimpMessagingTemplate webSocket;

    @Autowired
    private ActiveMatchesManager activeMatchesManager;

    @MessageMapping(URLs.ENTER_GAME)
    public void enterGame(@DestinationVariable String gameName) {
        Match match = activeMatchesManager.getMatch(gameName);
        GamePrep.setUpGameOrAddPlayer(match);
        webSocket.convertAndSend(URLs.UPDATE_GAME(gameName), match);
    }

    @MessageMapping(URLs.LEAVE_GAME)
    public void leaveGame(@DestinationVariable String gameName) {
        Match match = activeMatchesManager.getMatch(gameName);
        GamePrep.setUpGameOrAddPlayer(match);
        webSocket.convertAndSend(URLs.UPDATE_GAME(gameName), match);
    }

    @MessageMapping(URLs.START_HUMANS_GAME)
    public void startGame(@DestinationVariable String gameName) {
        Match match = activeMatchesManager.getMatch(gameName);
        GamePrep.setUpGameAndStart(match);
        webSocket.convertAndSend(URLs.UPDATE_GAME(gameName), match);
    }

    @MessageMapping(URLs.PLACE_BET)
    public void placeBet(@DestinationVariable String gameName, @Payload Bet bet) {
        Match match = activeMatchesManager.getMatch(gameName);
        try {
            match.getGame().placeBet(bet.getPlayerEmail(), bet.getBetValue());
            webSocket.convertAndSend(URLs.UPDATE_GAME(gameName), match);
            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
                sendChangedTurn_Delayed(match);
            }
        } catch (ArithmeticException e) {
            // send to just this user to let them know that they have less money than they bet
        }
    }

    private void sendChangedRound_Delayed(Match match) {
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                            match.getGame().nextRound();
                            webSocket.convertAndSend(URLs.UPDATE_GAME(match.getMatchName()), match);
                    }
                },
                5000
        );
    }


    private void sendChangedTurn_Delayed(Match match) {
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        match.getGame().nextTurn();
                        if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
                            sendChangedTurn_Delayed(match);
                        }
                        webSocket.convertAndSend(URLs.UPDATE_GAME(match.getMatchName()), match);
                        if (match.getGame().isVerdictOut()) {
                            sendChangedRound_Delayed(match);
                        }
                    }
                },
                5000
        );
    }

    @MessageMapping(URLs.STICK)
    public void stick(@DestinationVariable String gameName) {
        Match match = activeMatchesManager.getMatch(gameName);
        match.getGame().sticks();
        webSocket.convertAndSend(URLs.UPDATE_GAME(gameName), match);
        if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
            sendChangedTurn_Delayed(match);
        }
        if (match.getGame().isVerdictOut()) {
            sendChangedRound_Delayed(match);
        }
    }


    @MessageMapping(URLs.DRAW)
    public void draw(@DestinationVariable String gameName) {
        Match match = activeMatchesManager.getMatch(gameName);
        match.getGame().draws();
        webSocket.convertAndSend(URLs.UPDATE_GAME(gameName), match);
        if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
            sendChangedTurn_Delayed(match);
        }
        if (match.getGame().isVerdictOut()) {
            sendChangedRound_Delayed(match);
        }
    }
}
