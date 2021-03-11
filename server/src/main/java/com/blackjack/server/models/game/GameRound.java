package com.blackjack.server.models.game;

import java.util.HashMap;
import java.util.LinkedList;

public class GameRound extends Game {

    public GameRound(LinkedList<Player> players, Dealer dealer, Deck deck) {
        super(players, dealer, deck);
    }

    public GameRound(Dealer dealer, Deck deck) {
        super(dealer, deck);
    }

    public GameRound() {}

    private HashMap<String, Player> playerWon(Player dealer, Player player) {
        HashMap<String, Player> results = new HashMap<>();
        results.put("Winner", player);
        results.put("Loser", dealer);
        player.setStatus(PlayerStatus.WON);
        return results;
    }

    private HashMap<String, Player> dealerWon(Player dealer, Player player) {
        HashMap<String, Player> results = new HashMap<>();
        results.put("Winner", dealer);
        results.put("Loser", player);
        player.setStatus(PlayerStatus.LOST);
        return results;
    }

    public void verdict() {
        setVerdictOut(true);
        for (Player player : getPlayers()) {
            double bet = player.getBet();
            HashMap<String, Player> gameResults = whoWon(getDealer(), player);
            gameResults.get("Winner").increaseMoney(bet);
            gameResults.get("Loser").decreaseMoney(bet);
            player.setBet(0);
        }
    }

    public HashMap<String, Player> whoWon(Player dealer, Player player) {
        if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
            if (player.getStatus() == PlayerStatus.BLACKJACK) {
                if (dealer.getCards().size() <= player.getCards().size()) return dealerWon(dealer, player);
                if (dealer.getCards().size() > player.getCards().size()) return playerWon(dealer, player);
            }
            return dealerWon(dealer, player);
        }

        if (dealer.getStatus() == PlayerStatus.STICK) {
            if (player.getStatus() == PlayerStatus.BLACKJACK) return playerWon(dealer, player);
            if (player.getStatus() == PlayerStatus.BUSTED) return dealerWon(dealer, player);
            if (player.getStatus() == PlayerStatus.STICK) {
                if (dealer.cardTotal() >= player.cardTotal()) return dealerWon(dealer, player);
                if (dealer.cardTotal() < player.cardTotal()) return playerWon(dealer, player);
            }
        }

        if (dealer.getStatus() == PlayerStatus.BUSTED && player.getStatus() == PlayerStatus.BUSTED) return dealerWon(dealer, player);
        return playerWon(dealer, player);
    }

    public void startRound() {
        Player firstPlayer = getPlayers().get(0);
        firstPlayer.setStatus(PlayerStatus.PLAYING);
        if (firstPlayer.getStatus() == PlayerStatus.BLACKJACK) {
            setPlayerWhoJustGotDealtBlackJack(firstPlayer);
        }
    }

    public void placingBetsStatus() {
        getPlayers().forEach(player -> player.setStatus(PlayerStatus.BETTING));
        getDealer().setStatus(PlayerStatus.WAITING_TURN);
    }


    public void placeBet(String email, double bet) {
        Player player = getPlayerByEmail(email);
        try {
            player.setBet(bet);
            player.setStatus(PlayerStatus.WAITING_TURN);
            if (hasEveryoneBet()) startRound();
        } catch (ArithmeticException e) {
            throw new ArithmeticException("Not enough money");
        }
    }



    public void sticks() {
        // TODO: export these to different functions. line by line if it needs be
        //// ORDER MATTERS HERE ////
        Player playingPlayer = grabPlayingPlayer();
        if (playingPlayer.getIsDealer()) {
            playingPlayer.setStatus(PlayerStatus.STICK);
            verdict();
        } else {
            Player nextPlayer = grabNextPlayingPlayer();
            playingPlayer.setStatus(PlayerStatus.STICK);
            nextPlayer.setStatus(PlayerStatus.PLAYING);
            if (nextPlayer.getStatus() == PlayerStatus.BLACKJACK) {
                setPlayerWhoJustGotDealtBlackJack(nextPlayer);
            }
        }


    }

    public void draws() {
        // TODO: export these to different functions. line by line if it needs be
        //// ORDER MATTERS HERE ////
        Player playingPlayer = grabPlayingPlayer();

        if (playingPlayer.getIsDealer()) {
            // round will end -- current player is dealer
            playingPlayer.drawCard(getDeck().dealCard(CardVisibility.REVEALED));
            if (playingPlayer.getStatus() != PlayerStatus.PLAYING) {
                // playing player either busted or blackjacked
                verdict();
            } else {
                // playing player keeps playing
            }
        } else {
            // round will not end -- there is still another player after the current player
            Player nextPlayer = grabNextPlayingPlayer();
            playingPlayer.drawCard(getDeck().dealCard(CardVisibility.REVEALED));
            if (playingPlayer.getStatus() != PlayerStatus.PLAYING) {
                // playing player either busted or blackjacked
                if (nextPlayer.getIsDealer() && haveAllPlayersBusted()) {
                    // if next player is dealer and if everyone else is busted, send out verdict
                    nextPlayer.setStatus(PlayerStatus.STICK);
                    verdict();
                } else {
                    nextPlayer.setStatus(PlayerStatus.PLAYING);
                    if (nextPlayer.getStatus() == PlayerStatus.BLACKJACK) {
                        setPlayerWhoJustGotDealtBlackJack(nextPlayer);
                    }
                }
            } else {
                // playing player keeps playing
            }
        }
    }

    public void nextTurn() {
        // TODO: export these to different functions. line by line if it needs be
        //// ORDER MATTERS HERE ////
        Player playingPlayer = getPlayerWhoJustGotDealtBlackJack();
        setPlayerWhoJustGotDealtBlackJack(null);
        if (playingPlayer.getIsDealer()) {
            verdict();
        } else {
            Player nextPlayer = grabNextPlayingPlayer(playingPlayer);
            nextPlayer.setStatus(PlayerStatus.PLAYING);
            if (nextPlayer.getStatus() == PlayerStatus.BLACKJACK) {
                setPlayerWhoJustGotDealtBlackJack(nextPlayer);
            }
        }
    }

    public void nextRound() {
        LinkedList<Player> allPlayers = listOfAllPlayersAndDealer_LastPosition();
        allPlayers.forEach(player -> player.resetCards());
        getDeck().resetDeck();
        changeDealerIfBustedTwice();
        dealCards();
        placingBetsStatus();
        setVerdictOut(false);
    }

    private void changeDealerIfBustedTwice() {
        Dealer oldDealer = (Dealer) getDealer();

        if (oldDealer.getNumOfBusts() > 1) {

            oldDealer.setIsDealer(false);
            getPlayers().add(new Player(oldDealer));

            Dealer newDealer = new Dealer(getPlayers().remove(0));
            newDealer.setIsDealer(true);

            setDealer(newDealer);
        }
    }

    public boolean hasEveryoneBet() {
        boolean haveAllBet = true;
        for (Player player : getPlayers()) {
            if (player.getStatus() == PlayerStatus.BETTING) {
                haveAllBet = false;
                break;
            }
        }
        return haveAllBet;
    }

    public void dealCards() {
        oneRoundOfCards(CardVisibility.HIDDEN);
        oneRoundOfCards(CardVisibility.REVEALED);
    }

    private void oneRoundOfCards(CardVisibility cardVisibility) {
        getDealer().addCard(getDeck().dealCard(cardVisibility));
        for (Player player : getPlayers()) {
            player.addCard(getDeck().dealCard(cardVisibility));
        }
    }
}
