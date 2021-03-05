package com.blackjack.server.models.match;

import com.blackjack.server.models.game.Player;

import java.util.ArrayList;
import java.util.List;

public class Match {

    private final String matchName;
    private List<Player> players;
    private int duration;
    private final int maxNumberOfPlayers;
    private final GameType gameType;
    private String remoteAddr;
    private final GamePrivacy privacy;

    public Match(String matchName, int maxNumPlayers, GameType gameType, GamePrivacy privacy) {
        this.matchName = matchName;
        this.duration = 0;
        this.maxNumberOfPlayers = maxNumPlayers;
        this.gameType = gameType;
        this.players = new ArrayList<>();
        this.privacy = privacy;
    }

    public GamePrivacy getPrivacy() {
        return privacy;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getMatchName() {
        return matchName;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    public GameType getGameType() {
        return gameType;
    }
}
