package com.blackjack.server.models.game;

import java.util.HashMap;
import java.util.LinkedList;

public class Game {

    LinkedList<Player> players;
    Player dealer;
    Deck deck;


    public Game(LinkedList<Player> players, Player dealer, Deck deck) {
        this.players = players;
        this.dealer = dealer;
        this.deck = deck;
    }

    public void verdict() {
        for (Player player : players) {
            Bet bet = player.getBet();
            HashMap<String, Player> gameResults = GameRound.whoWon(dealer, player);
            gameResults.get("Winner").increaseMoney(bet.getBetValue());
            gameResults.get("Loser").decreaseMoney(bet.getBetValue());
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

    public void startRound() {
        GameRound.startRound(this);
    }

    public void nextTurn() {
        LinkedList<Player> allPlayers = new LinkedList<>(players);
        allPlayers.add(dealer);
        for (Player player : players) {
            if (player.getStatus() == PlayerStatus.PLAYING) {
                int indexOfCurrentPlayer = players.indexOf(player);
//                player.setStatus(P);
                Player nextPlayer = players.get(indexOfCurrentPlayer + 1);
                nextPlayer.setStatus(PlayerStatus.PLAYING);
                break;
            }
        }
    }



}
