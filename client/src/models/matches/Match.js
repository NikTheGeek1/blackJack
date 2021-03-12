import Game from './Game';

class Match {
    constructor(fetchedMatch) {
        this.matchName = fetchedMatch.matchName;
        this.maxNumberOfPlayers = fetchedMatch.maxNumberOfPlayers;
        this.gameType = fetchedMatch.gameType.toUpperCase();
        this.privacy = fetchedMatch.privacy.toUpperCase();
        this.duration = fetchedMatch.duration;
        this.onset = fetchedMatch.onset;
        this.users = fetchedMatch.users;
        this.game = fetchedMatch.game && new Game(fetchedMatch.game) ;
    }
}

export default Match;