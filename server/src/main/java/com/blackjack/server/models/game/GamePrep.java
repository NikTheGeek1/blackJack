package com.blackjack.server.models.game;

import com.blackjack.server.models.User;
import com.blackjack.server.models.match.Match;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GamePrep {

    public static void setUpGameOrAddPlayer (Match match) {

        if (match.getGame() != null) {
            // game is already initialised
            addNewlyArrivedUsersAsPlayers(match);
        } else {
            // initialise game
            // TODO: export this to a function
            Deck deck = new Deck();

            Dealer dealer = new Dealer(match.getUsers().get(0));
            dealer.setIsDealer(true);

            GameRound game = new GameRound(dealer, deck);
            match.setGame(game);
        }

    }

    private static void addNewlyArrivedUsersAsPlayers(Match match) {
        // TODO: export these to different functions
        LinkedList<Player> allPlayersAndDealer = match.getGame().listOfAllPlayersAndDealer_LastPosition();
        List<String> allPlayersAndDealerEmails = allPlayersAndDealer.stream()
                .map(player -> player.getEmail())
                .collect(Collectors.toList());
        LinkedList<User> allUsers = match.getUsers();

        List<User> newUsers = allUsers.stream()
                .filter(user -> !allPlayersAndDealerEmails.contains(user.getEmail()))
                .collect(Collectors.toList());

        for (User newUser : newUsers) {
            match.getGame().addPlayer(new Player(newUser));
        }
    }

    public static void setUpGameAndStart(Match match) {
        match.getGame().nextRound();
    }

}
