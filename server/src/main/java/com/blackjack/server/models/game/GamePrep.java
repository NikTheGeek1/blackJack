package com.blackjack.server.models.game;

import com.blackjack.server.models.User;
import com.blackjack.server.models.match.GameType;
import com.blackjack.server.models.match.Match;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GamePrep {

    public static void setUpGameOrAddPlayer (Match match) {
        if (match == null) return;
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
        if (match.getGameType() == GameType.HUMANS) {
            setUpHumansGame(match);
        } else {
            setUpComputerGame(match);
        }
    }

    private static void setUpHumansGame(Match match) {
        Deck deck = new Deck();
        Dealer dealer = new Dealer(match.getUsers().get(0));
        GameRound game = new GameRound(dealer, deck);
        match.setGame(game);
        dealer.setIsDealer(true);
    }

    private static void setUpComputerGame(Match match) {
        Deck deck = new Deck();
        Dealer dealer = new Dealer(new Player());
        dealer.setName("Dealer");
        dealer.setEmail("Dealer");
        dealer.setIsDealer(true);
        dealer.setMoney(100000);
        match.addUser(dealer);
        GameRound game = new GameRound(dealer, deck);
        match.setGame(game);
        match.getGame().addPlayer(new Player(match.getUsers().get(0)));
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
        match.getGame().nextRound(match.getGameType());
    }

    public static void clearLeaverDebts(Player leaver, Match match) {
        User leaverUser = match.getUserByEmail(leaver.getEmail());
        if (leaver.getIsDealer()) {
            match.getGame().getPlayers().forEach(player -> {
                double playersBet = player.getBet();
                leaver.decreaseMoney(playersBet);
                player.increaseMoney(playersBet);
            });
        } else {
            double leaversBet = leaver.getBet();
            leaver.decreaseMoney(leaversBet);
            match.getGame().getDealer().increaseMoney(leaversBet);
        }
        leaverUser.setMoney(leaver.getMoney());
    }

    public static void dropOutManager(Match match, String leaverEmail) {
        // TODO: refactor dropOutManager, decideIfOnlyOneOrManyPlayersLeftInGame, manyPlayersLeftInGame.
        // if the leaver is the playing player, we need to pass the PLAYING status to
        // the next player, but the current implementation is awful. There are 2 pairs of 2 functions (4 in total)
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
        match.getGame().setPlayerWhoJustGotDealtBlackJack(null);
        match.getGame().setDealer(newDealer);
        match.getGame().prepareDealerForNextRound(false);
        match.getGame().getPlayers().forEach(player -> match.getGame().preparePlayerForNextRound(player, false));
    }


    private static void playerLeftAlone(Match match) {
        if (match.getGame().getDealer() == null) {// if the leaver was the dealer
            Dealer newDealer = new Dealer(match.getGame().getPlayers().remove(0));
            match.getGame().setDealer(newDealer);
        }
        match.getGame().setPlayerWhoJustGotDealtBlackJack(null);
        match.getGame().prepareDealerForNextRound(false);
    }


    public static void playerSticks(Match match) {
        match.getGame().sticks();
        if (match.getGameType() == GameType.COMPUTER && match.getGame().getDealer().getStatus() == PlayerStatus.PLAYING) {
            match.setHasSimulationStared(true);
        }
    }
}
