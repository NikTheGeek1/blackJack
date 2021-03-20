package com.blackjack.server.models.game;

public class PlayerChoice {

    private String playerEmail;
    private PlayerChoiceType playerChoiceType;

    public PlayerChoice(String playerEmail, PlayerChoiceType playerChoiceType) {
        this.playerEmail = playerEmail;
        this.playerChoiceType = playerChoiceType;
    }

    public String getPlayerEmail() {
        return playerEmail;
    }

    public void setPlayerEmail(String playerEmail) {
        this.playerEmail = playerEmail;
    }

    public PlayerChoiceType getPlayerChoiceType() {
        return playerChoiceType;
    }

    public void setPlayerChoiceType(PlayerChoiceType playerChoiceType) {
        this.playerChoiceType = playerChoiceType;
    }
}
