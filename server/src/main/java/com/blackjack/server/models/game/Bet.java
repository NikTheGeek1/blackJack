package com.blackjack.server.models.game;

public class Bet {
    private final String playerEmail;
    private final double betValue;

    public Bet(String playerEmail, double betValue) {
        this.playerEmail = playerEmail;
        this.betValue = betValue;
    }

    public String getPlayerEmail() {
        return playerEmail;
    }

    public double getBetValue() {
        return betValue;
    }
}
