package com.blackjack.server.models.match;

public class PrivateMatch extends Match{

    private String matchPassword;

    public PrivateMatch(String matchName, String matchPassword, int maxNumberOfPlayers, GameType gameType, GamePrivacy privacy) {
        super(matchName, maxNumberOfPlayers, gameType, privacy);
        this.matchPassword = matchPassword;
    }

    public PrivateMatch(Match match, String matchPassword) {
        super(match.getMatchName(), match.getMaxNumberOfPlayers(), match.getGameType(), match.getPrivacy());
        this.matchPassword = matchPassword;
    }

    public String getMatchPassword() {
        return matchPassword;
    }

    public void setMatchPassword(String matchPassword) {
        this.matchPassword = matchPassword;
    }
}
