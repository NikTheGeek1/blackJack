package com.blackjack.server.models.game;

import java.util.HashMap;

public class Bet {
    private final String playerEmail;
    private final HashMap<String, Integer> betTokens;

    public Bet(String playerEmail, HashMap<String, Integer> betTokens) {
        this.playerEmail = playerEmail;
        this.betTokens = betTokens;
    }

    public String getPlayerEmail() {
        return playerEmail;
    }

    public HashMap<String, Integer> getBetTokens() {
        return betTokens;
    }
}
