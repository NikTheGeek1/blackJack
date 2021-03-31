package com.blackjack.server.websockets.controllers.game;

import com.blackjack.server.models.game.PlayerChoice;
import com.blackjack.server.models.match.Match;

public class UpdateGameResponse {
    private Match match;
    private PlayerChoice playerChoice;

    public UpdateGameResponse(Match match, PlayerChoice playerChoice) {
        this.match = match;
        this.playerChoice = playerChoice;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public PlayerChoice getPlayerChoice() {
        return playerChoice;
    }

    public void setPlayerChoice(PlayerChoice playerChoice) {
        this.playerChoice = playerChoice;
    }
}
