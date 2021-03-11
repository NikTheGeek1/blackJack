package com.blackjack.server.models.game;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;

public abstract class Game {

    private LinkedList<Player> players;
    private Dealer dealer;
    private LinkedList<Player> allPlayersDealerFirst;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Deck deck;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean isVerdictOut;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Player playerWhoJustGotDealtBlackJack;


    public Game(LinkedList<Player> players, Dealer dealer, Deck deck) {
        this.players = players;
        this.dealer = dealer;
        this.deck = deck;
        this.isVerdictOut = false;
        setAllPlayersDealerFirst();
    }

    public Game(Dealer dealer, Deck deck) {
        this.players = new LinkedList<>();
        this.dealer = dealer;
        this.deck = deck;
        this.isVerdictOut = false;
        this.allPlayersDealerFirst = new LinkedList<>();
        setAllPlayersDealerFirst();
    }

    public Game() { }


    public Player getPlayerWhoJustGotDealtBlackJack() {
        return playerWhoJustGotDealtBlackJack;
    }

    public void setPlayerWhoJustGotDealtBlackJack(Player hasNextPlayerBlackJacked) {
        this.playerWhoJustGotDealtBlackJack = hasNextPlayerBlackJacked;
    }


    public boolean isVerdictOut() {
        return isVerdictOut;
    }

    public void setVerdictOut(boolean verdictOut) {
        isVerdictOut = verdictOut;
    }


    public Player getPlayerByEmail(String playerEmail) {
        LinkedList<Player> allPlayers = listOfAllPlayersAndDealer_LastPosition();
        for (Player player : allPlayers) {
            if (player.getEmail().equals(playerEmail))
                return player;
        }
        return null;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
        setAllPlayersDealerFirst();
    }

    public LinkedList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(LinkedList<Player> players) {
        this.players = players;
        setAllPlayersDealerFirst();
    }

    public Player getDealer() {
        return dealer;
    }

    public void setDealer(Dealer dealer) {
        this.dealer = dealer;
        setAllPlayersDealerFirst();
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public LinkedList<Player> listOfAllPlayersAndDealer_LastPosition() {
        LinkedList<Player> allPlayers = new LinkedList<>(players);
        allPlayers.add(dealer);
        return allPlayers;
    }

    public LinkedList<Player> getAllPlayersDealerFirst() {
        return allPlayersDealerFirst;
    }

    public void setAllPlayersDealerFirst() {
        this.allPlayersDealerFirst.clear();
        this.allPlayersDealerFirst.add(dealer);
        this.allPlayersDealerFirst.addAll(players);
    }

    public Player grabPlayingPlayer () {
        LinkedList<Player> allPlayers = listOfAllPlayersAndDealer_LastPosition();
        for (Player player : allPlayers) {
            if (player.getStatus() == PlayerStatus.PLAYING) {
                return player;
            }
        }
        return null;
    }

    public Player grabNextPlayingPlayer() {
        LinkedList<Player> allPlayers = listOfAllPlayersAndDealer_LastPosition();
        int playersNum = allPlayers.size();
        for (int playerIdx = 0; playerIdx < playersNum; playerIdx++) {
            Player currPlayer = allPlayers.get(playerIdx);
            if (currPlayer.getStatus() == PlayerStatus.PLAYING) {
                return allPlayers.get(playerIdx+1);
            }
        }
        return null;
    }

    public Player grabNextPlayingPlayer(Player currentPlayer) {
        LinkedList<Player> allPlayers = listOfAllPlayersAndDealer_LastPosition();
        int idxOfCurrentPlayer = allPlayers.indexOf(currentPlayer);
        return allPlayers.get(idxOfCurrentPlayer+1);
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
