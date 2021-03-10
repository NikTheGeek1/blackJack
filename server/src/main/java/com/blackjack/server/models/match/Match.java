package com.blackjack.server.models.match;

import com.blackjack.server.models.game.Game;
import com.blackjack.server.models.game.Player;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.LinkedList;

public class Match {

    private final String matchName;
    private LinkedList<Player> players;
    private int duration;
    private Date onset;
    private final int maxNumberOfPlayers;
    private final GameType gameType;
    private final GamePrivacy privacy;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Game game;

    public Match(String matchName, int maxNumPlayers, GameType gameType, GamePrivacy privacy) {
        this.matchName = matchName;
        this.duration = 0;
        this.maxNumberOfPlayers = maxNumPlayers;
        this.gameType = gameType;
        this.players = new LinkedList<>();
        this.privacy = privacy;
        this.onset = new Date();
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
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

    public LinkedList<Player> getPlayers() {
        return players;
    }

    public boolean hasSpace() {
        return this.players.size() < this.maxNumberOfPlayers;
    }

    public void addPlayer(Player player) {
        if (hasSpace())
            players.add(player);
        else
            throw new IndexOutOfBoundsException("There is no space in that room");
    }

    public Player getPlayerByEmail(String playerEmail) {
        for (Player player : players) {
            if (player.getEmail().equals(playerEmail))
                return player;
        }
        return null;
    }

    public Player removePlayer(String playerEmail) {
        Player player = getPlayerByEmail(playerEmail);
        players.remove(player);
        return player;
    }

    public void setPlayers(LinkedList<Player> players) {
        this.players = players;
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
        return players.size() == 0;
    }
}
