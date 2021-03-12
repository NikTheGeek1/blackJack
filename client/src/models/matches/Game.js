import Dealer from './Dealer';

class Game {
    constructor(fetchedGame) {
        this.players = fetchedGame.players;
        this.dealer = new Dealer(fetchedGame.dealer);
        this.allPlayersDealerFirst = fetchedGame.allPlayersDealerFirst;
    }
}

export default Game;