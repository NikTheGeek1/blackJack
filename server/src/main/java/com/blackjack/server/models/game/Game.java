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
        this.players.add( player);
        setAllPlayersDealerFirst();
    }

    public LinkedList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(LinkedList<Player> players) {
        this.players = players;
        setAllPlayersDealerFirst();
    }

    public Dealer getDealer() {
        return dealer;
    }

    public void removeDealer() {
        this.dealer = null;
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
            if (isThisThePlayingPlayer(player)) {
                return player;
            }
        }
        return null;
    }

    public Player grabPreviousPlayer(Player currentPlayer) {
        LinkedList<Player> allPlayers = listOfAllPlayersAndDealer_LastPosition();
        int idxOfCurrentPlayer = allPlayers.indexOf(currentPlayer);
        if (idxOfCurrentPlayer == 0) return null;
        int i = 1; // this is the number which will be decrement to the current players idx to find the first playing player
        try {
            while (allPlayers.get(idxOfCurrentPlayer - i).getCards().isEmpty()) {
                i++;
            }
            return allPlayers.get(idxOfCurrentPlayer - i);
        } catch (IndexOutOfBoundsException e) {
            // ALL PLAYERS HAVE EMPTY HANDS. MEANING THAT ITS THE START OF THE GAME
            // SO PLAYER 1 GOES FIRST
            return allPlayers.get(0);
        }
    }

    public boolean isThisThePlayingPlayer(Player player) {
        if (player.getStatus() == PlayerStatus.PLAYING) return true;
        if (player.getStatus() == PlayerStatus.BETTING) return false;
        Player previousPlayer = grabPreviousPlayer(player);
        Player nextPlayer = grabNextPlayingPlayer(player);
        if (previousPlayer == null && (nextPlayer.getStatus() == PlayerStatus.WAITING_TURN)) {
            return true;
        }
        if (previousPlayer == null && (nextPlayer.getStatus() != PlayerStatus.WAITING_TURN)) {
            return false;
        }
        if (nextPlayer == null &&
                (previousPlayer.getStatus() == PlayerStatus.BLACKJACK ||
                        previousPlayer.getStatus() == PlayerStatus.STICK ||
                        previousPlayer.getStatus() == PlayerStatus.BUSTED)) {
            return true;
        }
        if (nextPlayer == null &&
                (previousPlayer.getStatus() != PlayerStatus.BLACKJACK &&
                        previousPlayer.getStatus() != PlayerStatus.STICK &&
                        previousPlayer.getStatus() != PlayerStatus.BUSTED)) {
            return false;
        }
        if (
                (previousPlayer.getStatus() == PlayerStatus.BLACKJACK ||
                        previousPlayer.getStatus() == PlayerStatus.STICK ||
                        previousPlayer.getStatus() == PlayerStatus.BUSTED)
                        &&
                        (nextPlayer.getStatus() == PlayerStatus.WAITING_TURN)
        ) {
            return true;
        }
        return false;
    }

    public Player grabNextPlayingPlayer() {
        LinkedList<Player> allPlayers = listOfAllPlayersAndDealer_LastPosition();
        int playersNum = allPlayers.size();
        for (int playerIdx = 0; playerIdx < playersNum; playerIdx++) {
            Player currPlayer = allPlayers.get(playerIdx);
            if (isThisThePlayingPlayer(currPlayer)) {
                int i = 1; // this is the number which will be added to the current players idx to find the first playing player
                while (allPlayers.get(playerIdx + i).getCards().isEmpty()) {
                    i++;
                }
                return allPlayers.get(playerIdx+i);
            }
        }
        return null;
    }


    public Player grabNextPlayingPlayer(Player currentPlayer) {
        LinkedList<Player> allPlayers = listOfAllPlayersAndDealer_LastPosition();
        int idxOfCurrentPlayer = allPlayers.indexOf(currentPlayer);
        if (idxOfCurrentPlayer == allPlayers.size() - 1) return null;
        int i = 1; // this is the number which will be added to the current players idx to find the first playing player
        try {
            while (allPlayers.get(idxOfCurrentPlayer + i).getCards().isEmpty()) {
                i++;
            }
            return allPlayers.get(idxOfCurrentPlayer+i);
        } catch (IndexOutOfBoundsException e) {
            // ALL PLAYERS HAVE EMPTY HANDS. MEANING THAT ITS THE START OF THE GAME
            // SO PLAYER 1 GOES FIRST
            return allPlayers.get(0);
        }
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

    public boolean haveAllPlayersBJ() {
        boolean haveAllBJ = true;
        for (Player player : players) {
            if (player.getStatus() != PlayerStatus.BLACKJACK) {
                haveAllBJ = false;
                break;
            }
        }
        return haveAllBJ;
    }

}
