package com.blackjack.server.models.game;

import com.blackjack.server.models.User;
import com.blackjack.server.models.match.Match;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GamePrep {

    public static void setUpGameOrAddPlayer (Match match) {
        // playerWaitGameToStart as opposed to wait for their turn
        if (match.getGame() != null) {
            // game is already initialised

            addNewlyArrivedUsersAsPlayers(match, false);
        } else {
            // initialise game
            setUpGame(match);
        }
    }

    private static boolean hasTheGameStarted(Match match) {
        boolean isTheGameStarted = true;
        for (Player player : match.getGame().getAllPlayersDealerFirst()) {
            if (player.getStatus() == PlayerStatus.WAITING_GAME) {
                isTheGameStarted = false;
                break;
            }
        }
        return isTheGameStarted;
    }

    private static void setUpGame(Match match) {
        Deck deck = new Deck();

        Dealer dealer = new Dealer(match.getUsers().get(0));
        dealer.setIsDealer(true);

        GameRound game = new GameRound(dealer, deck);
        match.setGame(game);
    }

    private static void addNewlyArrivedUsersAsPlayers(Match match, boolean hasTheGameStarted) {
        // playerWaitGameToStart as opposed to waiting for their turn
        LinkedList<Player> playersInGame = match.getGame().listOfAllPlayersAndDealer_LastPosition();
        LinkedList<User> allUsers = match.getUsers();
        List<User> newUsers = getUsersWhoAreNotRegisteredAsPlayers(playersInGame, allUsers);

        for (User newUser : newUsers) {
            Player newPlayer = new Player(newUser);
            match.getGame().preparePlayerForNextRound(newPlayer, hasTheGameStarted);
            match.getGame().addPlayer(newPlayer);
        }
    }

    private static List<User> getUsersWhoAreNotRegisteredAsPlayers(LinkedList<Player> playersInGame, LinkedList<User> allUsers) {
        List<String> allPlayersAndDealerEmails = playersInGame.stream()
                .map(player -> player.getEmail())
                .collect(Collectors.toList());

        List<User> newUsers = allUsers.stream()
                .filter(user -> !allPlayersAndDealerEmails.contains(user.getEmail()))
                .collect(Collectors.toList());

        return newUsers;
    }

    public static void startGame(Match match) {
        match.getGame().nextRound();
    }

    public static void clearLeaverDebts(Player leaver, Match match) {
        if (leaver.getIsDealer()) {
            match.getGame().getPlayers().forEach(player -> {
                double playersBet = player.getBet();
                User leaverUser = match.getUserByEmail(leaver.getEmail());
                leaverUser.decreaseMoney(playersBet);
                player.increaseMoney(playersBet);
            });
        } else {
            double leaversBet = leaver.getBet();
            User leaverUser = match.getUserByEmail(leaver.getEmail());
            leaverUser.decreaseMoney(leaversBet);
            match.getGame().getDealer().increaseMoney(leaversBet);
        }
    }

    public static void dropOutManager(Match match, String leaverEmail) {
        // TODO: refactor dropOutManager, decideIfOnlyOneOrManyPlayersLeftInGame, manyPlayersLeftInGame.
        // if the leaver is the playing player, we need to pass the PLAYING status to
        // the next player, but the current implementation if awful. There are 2 pair of 2 functions (4 in total)
        // which essentially doing the same thing. They are overloaded.
        Player leaver = match.getGame().getPlayerByEmail(leaverEmail);
        clearLeaverDebts(leaver, match);
        if (match.getGame().isThisThePlayingPlayer(leaver)) {
            Player nextPlayingPlayer = match.getGame().grabNextPlayingPlayer(leaver);
            decideIfOnlyOneOrManyPlayersLeftInGame(match, leaverEmail, nextPlayingPlayer);
        } else {
            decideIfOnlyOneOrManyPlayersLeftInGame(match, leaverEmail);
        }
    }

    public static void decideIfOnlyOneOrManyPlayersLeftInGame(Match match, String leaverEmail) {
        match.removeUser(leaverEmail);
        if (match.getGame().getAllPlayersDealerFirst().size() == 1) {
            playerLeftAlone(match);
        } else {
            manyPlayersLeftInGame(match);
        }
    }

    public static void decideIfOnlyOneOrManyPlayersLeftInGame(Match match, String leaverEmail, Player nextPlayingPlayer) {
        match.removeUser(leaverEmail);
        if (match.getGame().getAllPlayersDealerFirst().size() == 1) {
            playerLeftAlone(match);
        } else {
            manyPlayersLeftInGame(match, nextPlayingPlayer);
        }
    }

    private static void manyPlayersLeftInGame(Match match) {
        if (match.getGame().getDealer() == null) {// if the leaver was the dealer
            dealerLeftOtherStayed(match);
        } else {
            // keep playing
        }
    }

    private static void manyPlayersLeftInGame(Match match, Player nextPlayingPlayer) {
        if (match.getGame().getDealer() == null) {// if the leaver was the dealer
            dealerLeftOtherStayed(match);
        } else {
            // keep playing, but make sure that someone is playing.
            match.getGame().putStatusPlayingAndCheckIfBJ(nextPlayingPlayer);
        }
    }

    private static void dealerLeftOtherStayed(Match match) {
        Dealer newDealer = new Dealer(match.getGame().getPlayers().remove(0));
        match.getGame().setDealer(newDealer);
        match.getGame().prepareDealerForNextRound(true);
        match.getGame().getPlayers().forEach(player -> match.getGame().preparePlayerForNextRound(player, true));
    }


    private static void playerLeftAlone(Match match) {
        if (match.getGame().getDealer() == null) {// if the leaver was the dealer
            Dealer newDealer = new Dealer(match.getGame().getPlayers().remove(0));
            match.getGame().setDealer(newDealer);
        }
        match.getGame().setPlayerWhoJustGotDealtBlackJack(null);
        match.getGame().prepareDealerForNextRound(true);
    }


}
