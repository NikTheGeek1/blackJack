package com.blackjack.server.websockets.controllers.game;

import com.blackjack.server.models.game.Bet;
import com.blackjack.server.models.game.GamePrep;
import com.blackjack.server.models.game.PlayerChoice;
import com.blackjack.server.models.game.PlayerChoiceType;
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
        PlayerChoice playerChoice = new PlayerChoice(null, PlayerChoiceType.NULL);
        webSocket.convertAndSend(URLs.UPDATE_GAME(gameName), new UpdateGameResponse(match, playerChoice));
    }

    @MessageMapping(URLs.START_GAME)
    public void startGame(@DestinationVariable String gameName, @DestinationVariable String playerEmail) {
        System.out.println("here");
        Match match = activeMatchesManager.getMatch(gameName);
        GamePrep.startGame(match);
        PlayerChoice playerChoice = new PlayerChoice(playerEmail, PlayerChoiceType.STARTED_GAME);
        webSocket.convertAndSend(URLs.UPDATE_GAME(gameName), new UpdateGameResponse(match, playerChoice));
    }

    @MessageMapping(URLs.LEAVE_GAME)
    public void leaveGame(@DestinationVariable String gameName, @Payload String userEmail) {
        System.out.println("removing user from match");
        Match match = activeMatchesManager.getMatch(gameName);
        if (match == null) return; // todo: possible return a message saying that couldn't find match
        if (match.getUsers() != null && match.getUsers().size() == 1) {// the leaver is the last player
            activeMatchesManager.remove(gameName);
        } else {
            GamePrep.dropOutManager(match, userEmail);
        }
        PlayerChoice playerChoice = new PlayerChoice(userEmail, PlayerChoiceType.LEFT_GAME);
        webSocket.convertAndSend(URLs.UPDATE_GAME(gameName), new UpdateGameResponse(match, playerChoice));
    }

    @MessageMapping(URLs.PLACE_BET)
    public void placeBet(@DestinationVariable String gameName, @Payload Bet bet) {
        Match match = activeMatchesManager.getMatch(gameName);
        try {
            match.getGame().placeBet(bet.getPlayerEmail(), bet.getBetTokens());
            if (match.getGame().hasEveryoneBet()) match.getGame().startRound(match);
            PlayerChoice playerChoice = new PlayerChoice(bet.getPlayerEmail(), PlayerChoiceType.BET);
            webSocket.convertAndSend(URLs.UPDATE_GAME(gameName), new UpdateGameResponse(match, playerChoice));
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
                            match.getGame().nextRound(match.getGameType());
                        PlayerChoice playerChoice = new PlayerChoice(null, PlayerChoiceType.NULL);
                        webSocket.convertAndSend(URLs.UPDATE_GAME(match.getMatchName()), new UpdateGameResponse(match, playerChoice));
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
                        match.getGame().nextTurn(match);
                        if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
                            sendChangedTurn_Delayed(match);
                        }
                        PlayerChoice playerChoice = new PlayerChoice(null, PlayerChoiceType.NULL);
                        webSocket.convertAndSend(URLs.UPDATE_GAME(match.getMatchName()), new UpdateGameResponse(match, playerChoice));
                        if (match.getGame().isVerdictOut()) {
                            sendChangedRound_Delayed(match);
                        }
                    }
                },
                5000
        );
    }

    private void sendDealerNextMove_Delayed(Match match) {
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        match.getGame().nextSimulatedTurn(match);
                        if (match.getHasSimulationStared()) {
                            sendDealerNextMove_Delayed(match);
                        }
                        PlayerChoice playerChoice = new PlayerChoice(null, PlayerChoiceType.NULL);
                        webSocket.convertAndSend(URLs.UPDATE_GAME(match.getMatchName()), new UpdateGameResponse(match,playerChoice));
                        if (match.getGame().isVerdictOut()) {
                            sendChangedRound_Delayed(match);
                        }
                    }
                },
                5000
        );
    }

    @MessageMapping(URLs.STICK)
    public void stick(@DestinationVariable String gameName, @DestinationVariable String playerEmail) {
        Match match = activeMatchesManager.getMatch(gameName);
        GamePrep.playerSticks(match);
        PlayerChoice playerChoice = new PlayerChoice(playerEmail, PlayerChoiceType.STUCK);
        webSocket.convertAndSend(URLs.UPDATE_GAME(gameName), new UpdateGameResponse(match, playerChoice));
        if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
            sendChangedTurn_Delayed(match);
        }
        if (match.getGame().isVerdictOut()) {
            sendChangedRound_Delayed(match);
        }
        if (match.getHasSimulationStared()) {
            sendDealerNextMove_Delayed(match);
        }
    }


    @MessageMapping(URLs.DRAW)
    public void draw(@DestinationVariable String gameName, @DestinationVariable String playerEmail) {
        Match match = activeMatchesManager.getMatch(gameName);
        match.getGame().draws(match);
        PlayerChoice playerChoice = new PlayerChoice(playerEmail, PlayerChoiceType.DREW);
        webSocket.convertAndSend(URLs.UPDATE_GAME(gameName), new UpdateGameResponse(match, playerChoice));
        if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
            sendChangedTurn_Delayed(match);
        }
        if (match.getGame().isVerdictOut()) {
            sendChangedRound_Delayed(match);
        }
        if (match.getHasSimulationStared()) {
            sendDealerNextMove_Delayed(match);
        }
    }
}
