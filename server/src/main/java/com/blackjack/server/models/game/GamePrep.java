package com.blackjack.server.models.game;

import com.blackjack.server.models.match.Match;

import java.util.LinkedList;

public class GamePrep {

    public static void calibrateGame (Match match) {
        if (!hasDealer(match.getPlayers()) && hasPlayers(match)) {
            Player firstPlayer = match.getPlayers().get(0);
            firstPlayer.setIsDealer(true);
        }
    }

    public static boolean hasPlayers (Match match) {
        return match.getPlayers().size() > 0;
    }

    private static boolean hasDealer (LinkedList<Player> players) {
        boolean isDealer = false;
        for (Player player : players) {
            if (player.getIsDealer()) {
                isDealer = true;
                break;
            }
        }
        return isDealer;
    }

    private static Player getDealer (LinkedList<Player> players) {
        for (Player player : players) {
            if (player.getIsDealer()) {
                return player;
            }
        }
        return null;
    }

    private static LinkedList<Player> getPlayersOtherThanDealer (LinkedList<Player> players) {
        LinkedList<Player> playersOtherThanDealer = new LinkedList<>();
        players.forEach(player -> {
            if (!player.getIsDealer())
                playersOtherThanDealer.add(player);
        });
        return playersOtherThanDealer;
    }

    public static void startGame(Match match) {
        Deck deck = new Deck();
        Player dealer = getDealer(match.getPlayers());
        LinkedList<Player> playersOtherThanDealer = getPlayersOtherThanDealer(match.getPlayers());
        Game game = new Game(playersOtherThanDealer, dealer, deck);
        deck.shuffle();
        game.dealCards();
        game.startRound();
    }
}
