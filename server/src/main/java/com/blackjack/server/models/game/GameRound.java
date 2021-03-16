package com.blackjack.server.models.game;

import com.blackjack.server.models.match.GameType;
import com.blackjack.server.models.match.Match;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    public void verdict(Match thisMatch) {
        thisMatch.setHasSimulationStared(false);
        verdict();
    }

    public void verdict() {
        setVerdictOut(true);
        for (Player player : getPlayers()) {
            if (player.getStatus() == PlayerStatus.WAITING_TURN) continue;
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

    public void startRound(Match match) {
        Player firstPlayer = fetchFirstPlayingPlayer();
        putStatusPlayingAndCheckIfBJ(firstPlayer);
        if (firstPlayer.getIsDealer() && match.getGameType() == GameType.COMPUTER) {
            match.setHasSimulationStared(true);
        }
    }

    private Player fetchFirstPlayingPlayer() {
        for (Player player : getPlayers()) {
            if (!player.getCards().isEmpty()) return player;
        }
        return null;
    }

    public void placingBetsForPlayersStatus() {
        getPlayers().forEach(player -> player.setStatus(PlayerStatus.BETTING));
    }


    public void placeBet(String email, double bet) {
        Player player = getPlayerByEmail(email);
        try {
            player.setBet(bet);
            player.setStatus(PlayerStatus.WAITING_TURN);
        } catch (ArithmeticException e) {
            throw new ArithmeticException("Not enough money");
        }
    }

    @JsonIgnore
    public boolean isNextPlayerDealer() {
        Player nextPlayer = grabNextPlayingPlayer();
        return nextPlayer.getIsDealer();
    }

    public void sticks() {
        //// ORDER MATTERS HERE ////
        Player playingPlayer = grabPlayingPlayer();
        playingPlayer.setStatus(PlayerStatus.STICK);
        if (playingPlayer.getIsDealer()) {
            verdict();
        } else {
            Player nextPlayer = grabNextPlayingPlayer();
            putStatusPlayingAndCheckIfBJ(nextPlayer);
        }
    }

    public void putStatusPlayingAndCheckIfBJ(Player player) {
        player.setStatus(PlayerStatus.PLAYING);
        if (player.getStatus() == PlayerStatus.BLACKJACK) {
            setPlayerWhoJustGotDealtBlackJack(player);
        }
    }

    public void draws(Match match) {
        //// ORDER MATTERS HERE ////
        Player playingPlayer = grabPlayingPlayer();

        if (playingPlayer.getIsDealer()) {
            // round will end -- current player is dealer
            playingPlayer.drawCard(getDeck().dealCard(CardVisibility.REVEALED));
            if (playingPlayer.getStatus() != PlayerStatus.PLAYING) { // playing player either busted or blackjacked
                verdict();
            } else {}// playing player keeps playing (we're doing nothing yet)

        } else {// round will not end -- there is still another player after the current player
            Player nextPlayer = grabNextPlayingPlayer();
            playingPlayer.drawCard(getDeck().dealCard(CardVisibility.REVEALED));
            if (playingPlayer.getStatus() != PlayerStatus.PLAYING) {// playing player either busted or blackjacked
                if (nextPlayer.getIsDealer() && haveAllPlayersBusted()) {// if next player is dealer and if everyone else is busted, send out verdict
                    nextPlayer.setStatus(PlayerStatus.STICK);
                    verdict();
                } else {
                    putStatusPlayingAndCheckIfBJ(nextPlayer);
                    if (nextPlayer.getIsDealer() && match.getGameType() == GameType.COMPUTER) {
                        match.setHasSimulationStared(true);
                    }
                }


            } else {}// playing player keeps playing
        }
    }

    public void nextTurn(Match match) {
        //// ORDER MATTERS HERE ////
        Player playingPlayer = getPlayerWhoJustGotDealtBlackJack();
        setPlayerWhoJustGotDealtBlackJack(null);
        if (playingPlayer.getIsDealer()) {
            verdict();
        } else {
            Player nextPlayer = grabNextPlayingPlayer(playingPlayer);
            putStatusPlayingAndCheckIfBJ(nextPlayer);
            if (nextPlayer.getIsDealer() && match.getGameType() == GameType.COMPUTER) {
                match.setHasSimulationStared(true);
            }
        }
    }

    public void nextRound(GameType gameType) {
        if (gameType == GameType.HUMANS) {
            changeDealerIfBusted();
        }
        getPlayers().forEach(player -> preparePlayerForNextRound(player, true));
        getDeck().resetDeck();
        prepareDealerForNextRound(true);
        dealCards();
        placingBetsForPlayersStatus();
        setVerdictOut(false);
    }

    public void preparePlayerForNextRound(Player player, boolean hasTheGameStarted) {
        player.setIsDealer(false);
        player.resetCards();
        player.setBet(0);
        if (hasTheGameStarted) {
            player.setStatus(PlayerStatus.WAITING_TURN);
        } else {
            player.setStatus(PlayerStatus.WAITING_GAME);
        }
    }

    public void prepareDealerForNextRound(boolean hasTheGameStarted) {
        getDealer().setIsDealer(true);
        getDealer().resetCards();
        getDealer().setBet(0);
        if (hasTheGameStarted) {
            getDealer().setStatus(PlayerStatus.WAITING_TURN);
        } else {
            getDealer().setStatus(PlayerStatus.WAITING_GAME);
        }
    }

    private void changeDealerIfBusted() {
        Dealer oldDealer = (Dealer) getDealer();
        // wants to do the dealer for more or less than numOfMaxBusts busts
        int numOfMaxBusts = 2;
        if (oldDealer.getNumOfBusts() >= numOfMaxBusts) {
            Dealer newDealer = new Dealer(getPlayers().remove(0));
            swapDealer(newDealer);
        }
    }

    public void swapDealer(Dealer newDealer) {
        Dealer oldDealer = (Dealer) getDealer();
        oldDealer.setIsDealer(false);
        getPlayers().add(new Player(oldDealer));
        newDealer.setIsDealer(true);
        setDealer(newDealer);
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

    public void removePlayerByEmail(String playerEmail) {
        Player player = getPlayerByEmail(playerEmail);
        getPlayers().remove(player);
        removePlayerInAllPlayersDealerFirstByEmail(playerEmail);
        if (getDealer().getEmail().equals(playerEmail)) removeDealer();
        if (getPlayerWhoJustGotDealtBlackJack() != null &&
                getPlayerWhoJustGotDealtBlackJack().getEmail().equals(playerEmail))
            setPlayerWhoJustGotDealtBlackJack(null);
    }


    public void removePlayerInAllPlayersDealerFirstByEmail(String playerEmail) {
        for (int playerIdx = 0; playerIdx < getAllPlayersDealerFirst().size(); playerIdx++) {
            Player player = getAllPlayersDealerFirst().get(playerIdx);
            if (player.getEmail().equals(playerEmail)) getAllPlayersDealerFirst().remove(player);
        }
    }


    public void nextSimulatedTurn(Match thisMatch) {
        if (haveAllPlayersBJ() && getDealer().cardTotal() < 21) {
            getDealer().drawCard(getDeck().dealCard(CardVisibility.REVEALED));
            if (getDealer().getStatus() != PlayerStatus.PLAYING) { // playing player either busted or blackjacked
                verdict(thisMatch);
                return;
            }
        }
        if (getDealer().getStatus() ==  PlayerStatus.BLACKJACK) {
            verdict(thisMatch);
        } else if (getDealer().getStatus() == PlayerStatus.PLAYING) {
            if (getDealer().cardTotal() < 17) {
                // draws
                getDealer().drawCard(getDeck().dealCard(CardVisibility.REVEALED));
                if (getDealer().getStatus() != PlayerStatus.PLAYING) { // playing player either busted or blackjacked
                    verdict(thisMatch);
                }
            } else if (getDealer().cardTotal() > 16 && getDealer().cardTotal() < 21) {
                // sticks
                getDealer().setStatus(PlayerStatus.STICK);
                verdict(thisMatch);
            }
        } else {
            // TODO: DELETE THIS. IT'S ONLY FOR TESTING
            System.out.println("ERROR: at this point, dealer can only be BJ or Playing, everything else is wrong. But dealer is " + getDealer().getStatus());
        }

    }
}
