class Match {
    constructor(matchName, maxNumberOfPlayers, gameType, privacy, duration, onset, players) {
        this.matchName = matchName;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.gameType = gameType.toUpperCase();
        this.privacy = privacy.toUpperCase();
        this.duration = duration;
        this.onset = onset;
        this.players = players;
    }
}

export default Match;