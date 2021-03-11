class Match {
    constructor(matchName, maxNumberOfPlayers, gameType, privacy, duration, onset, users, game) {
        this.matchName = matchName;
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.gameType = gameType.toUpperCase();
        this.privacy = privacy.toUpperCase();
        this.duration = duration;
        this.onset = onset;
        this.users = users;
        this.game = game;
    }
}

export default Match;