package com.blackjack.server.models.game;

import com.blackjack.server.models.match.Match;

import java.util.HashMap;
import java.util.LinkedList;

public class Game {

    private LinkedList<Player> players;
    private Player dealer;
    private Deck deck;
    private boolean isVerdictOut;


    public Game(LinkedList<Player> players, Player dealer, Deck deck) {
        this.players = players;
        this.dealer = dealer;
        this.deck = deck;
        this.isVerdictOut = false;
    }

    public boolean isVerdictOut() {
        return isVerdictOut;
    }

    public void setVerdictOut(boolean verdictOut) {
        isVerdictOut = verdictOut;
    }

    public void verdict() {
        setVerdictOut(true);
        for (Player player : players) {
            double bet = player.getBet();
            HashMap<String, Player> gameResults = GameRound.whoWon(dealer, player);
            gameResults.get("Winner").increaseMoney(bet);
            gameResults.get("Loser").decreaseMoney(bet);
            player.setBet(0);
        }
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

    public LinkedList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(LinkedList<Player> players) {
        this.players = players;
    }

    public Player getDealer() {
        return dealer;
    }

    public void setDealer(Player dealer) {
        this.dealer = dealer;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }


    public void startRound() {
        GameRound.startRound(this);
    }

    private LinkedList<Player> addDealerToListOfPlayers_LastPosition () {
        LinkedList<Player> allPlayers = new LinkedList<>(players);
        allPlayers.add(dealer);
        return allPlayers;
    }

    public boolean hasEveryoneBet() {
        boolean haveAllBet = true;
        for (Player player : players) {
            if (player.getStatus() == PlayerStatus.BETTING) {
                haveAllBet = false;
                break;
            }
        }
        return haveAllBet;
    }


    public void placingBets () {
        players.forEach(player -> player.setStatus(PlayerStatus.BETTING));
        dealer.setStatus(PlayerStatus.WAITING_TURN);
    }

    public Player grabPlayingPlayer () {
        LinkedList<Player> allPlayers = addDealerToListOfPlayers_LastPosition();
        for (Player player : allPlayers) {
            if (player.getStatus() == PlayerStatus.PLAYING) {
                return player;
            }
        }
        return null;
    }

    public Player grabNextPlayingPlayer() {
        LinkedList<Player> allPlayers = addDealerToListOfPlayers_LastPosition();
        int playersNum = allPlayers.size();
        for (int playerIdx = 0; playerIdx < playersNum; playerIdx++) {
            Player currPlayer = allPlayers.get(playerIdx);
            if (currPlayer.getStatus() == PlayerStatus.PLAYING) {
                return allPlayers.get(playerIdx+1);
            }
        }
        return null;
    }

    public void nextRound() {
        LinkedList<Player> allPlayers = addDealerToListOfPlayers_LastPosition();
        allPlayers.forEach(player -> player.resetCards());
        deck.resetDeck();
        dealCards();
        placingBets();
        setVerdictOut(false);
    }

    public boolean haveAllPlayersBusted() {
        boolean haveAllBusted = true;
        for (Player player : players) {
            if (player.getStatus() != PlayerStatus.BUSTED) {
                haveAllBusted = false;
                break;
            }
        }
        return haveAllBusted;
    }
}
