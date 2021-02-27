package com.blackjack.server.models.game;

import java.util.HashMap;
import java.util.List;

public class Game {

    List<Player> players;
    Player dealer;
    Deck deck;


    public Game(List<Player> players, Player dealer, Deck deck) {
        this.players = players;
        this.dealer = dealer;
        this.deck = deck;
    }

    public void verdict() {
        for (Player player : players) {
            double bet = player.getBet();
            HashMap<String, Player> gameResults = GameRound.whoWon(dealer, player);
            gameResults.get("Winner").increaseMoney(bet);
            gameResults.get("Loser").decreaseMoney(bet);
            gameResults.get("Winner").prepareForNextRound();
            gameResults.get("Loser").prepareForNextRound();
        }
    }
}
