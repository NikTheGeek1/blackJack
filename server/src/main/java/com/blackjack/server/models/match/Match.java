package com.blackjack.server.models.match;

import com.blackjack.server.models.User;
import com.blackjack.server.models.game.GameRound;

import java.util.Date;
import java.util.LinkedList;

public class Match {
    // TODO: FRONT-END TODO: make a Game class and a Dealer class
    private final String matchName;
    private LinkedList<User> users;
    private int duration;
    private Date onset;
    private final int maxNumberOfPlayers;
    private final GameType gameType;
    private final GamePrivacy privacy;
    private GameRound game;

    public Match(String matchName, int maxNumberOfPlayers, GameType gameType, GamePrivacy privacy) {
        this.matchName = matchName;
        this.duration = 0;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.gameType = gameType;
        this.users = new LinkedList<User>();
        this.privacy = privacy;
        this.onset = new Date();
    }

    public GameRound getGame() {
        return game;
    }

    public void setGame(GameRound game) {
        this.game = game;
    }

    public Date getOnset() {
        return onset;
    }

    public void setOnset(Date onset) {
        this.onset = onset;
    }

    public GamePrivacy getPrivacy() {
        return privacy;
    }

    public String getMatchName() {
        return matchName;
    }

    public LinkedList<User> getUsers() {
        return users;
    }

    public boolean hasSpace() {
        return this.users.size() < this.maxNumberOfPlayers;
    }

    public void addUser(User user) {
        if (hasSpace())
            users.add(user);
        else
            throw new IndexOutOfBoundsException("There is no space in that room");
    }

    public User getPlayerByEmail(String playerEmail) {
        for (User player : users) {
            if (player.getEmail().equals(playerEmail))
                return player;
        }
        return null;
    }

    public User removeUser(String userEmail) {
        // TODO: when removing a user from the match, also remove them from the game
        User user = getPlayerByEmail(userEmail);
        users.remove(user);
        return user;
    }

    public void setUsers(LinkedList<User> users) {
        this.users = users;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration() {
        int diffInMinutes = (int) (new Date().getTime() - onset.getTime()) / (60 * 1000);
        this.duration = diffInMinutes;
    }

    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    public GameType getGameType() {
        return gameType;
    }

    public boolean isEmpty() {
        return users.size() == 0;
    }
}
