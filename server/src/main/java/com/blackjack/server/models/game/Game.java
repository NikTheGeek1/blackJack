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
            player.prepareForNextRound();
        }
        dealer.prepareForNextRound();
        deck.resetDeck();
    }

    public void dealCards() {
        oneRoundOfCards(CardVisibility.HIDDEN);
        oneRoundOfCards(CardVisibility.REVEALED);
    }

    private void oneRoundOfCards(CardVisibility cardVisibility) {
        dealer.addCard(deck.dealCard(cardVisibility));
        for (Player player : players) {
            player.addCard(deck.dealCard(cardVisibility));
        }
    }
}
