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
        match.setGame(game);
        game.nextRound();
    }

    public static void placeBet(Match match, String email, double bet) {
        Player player = match.getPlayerByEmail(email);
        try {
            player.setBet(bet);
            player.setStatus(PlayerStatus.WAITING_TURN);
            if (match.getGame().hasEveryoneBet()) match.getGame().startRound();
        } catch (ArithmeticException e) {
            throw new ArithmeticException("Not enough money");
        }
    }



    public static void sticks(Match match) {
        //// ORDER MATTERS HERE ////
        Player playingPlayer = match.getGame().grabPlayingPlayer();
        if (playingPlayer.getIsDealer()) {
            playingPlayer.setStatus(PlayerStatus.STICK);
            match.getGame().verdict();
        } else {
            Player nextPlayer = match.getGame().grabNextPlayingPlayer();
            playingPlayer.setStatus(PlayerStatus.STICK);
            nextPlayer.setStatus(PlayerStatus.PLAYING);
        }


    }

    public static void draws(Match match) {
        //// ORDER MATTERS HERE ////
        Player playingPlayer = match.getGame().grabPlayingPlayer();

        if (playingPlayer.getIsDealer()) {
            // round will end -- current player is dealer
            playingPlayer.drawCard(match.getGame().getDeck().dealCard(CardVisibility.REVEALED));
            if (playingPlayer.getStatus() != PlayerStatus.PLAYING) {
                // playing player either busted or blackjacked
                match.getGame().verdict();
            } else {
                // playing player keeps playing
            }
        } else {
            // round will not end -- there is still another player after the current player
            Player nextPlayer = match.getGame().grabNextPlayingPlayer();
            playingPlayer.drawCard(match.getGame().getDeck().dealCard(CardVisibility.REVEALED));
            if (playingPlayer.getStatus() != PlayerStatus.PLAYING) {
                // playing player either busted or blackjacked
                if (nextPlayer.getIsDealer() && match.getGame().haveAllPlayersBusted()) {
                    // if next player is dealer and if everyone else is busted, send out verdice
                    nextPlayer.setStatus(PlayerStatus.STICK);
                    match.getGame().verdict();
                } else {
                    nextPlayer.setStatus(PlayerStatus.PLAYING);
                }
            } else {
                // playing player keeps playing
            }
        }


    }

    public static void nextRound(Match match) {
        match.getGame().nextRound();
    }
}
