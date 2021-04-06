import Dealer from './Dealer';
import Player from './Player';

class Game {
    constructor(fetchedGame, deepCopyAllPlayers) {
        if (deepCopyAllPlayers) {
            this.allPlayersDealerFirst = this._deepCopyAllPlayers(fetchedGame.allPlayersDealerFirst);
        } else {
            this.allPlayersDealerFirst = fetchedGame.allPlayersDealerFirst.map(player => new Player(player));
        }
        this.players = fetchedGame.players.map(player => new Player(player));
        this.dealer = new Dealer(fetchedGame.dealer);
        this.verdictOut = fetchedGame.verdictOut;
    }

    _deepCopyAllPlayers(allPlayersDealerFirst) {
        const copiedAllPlayers = [];
        allPlayersDealerFirst.forEach(player => {
            const copiedCards = [];
            player.displayedCards.forEach(card => copiedCards.push(card));
            copiedAllPlayers.push({ ...player, displayedCards: copiedCards, });
        });
        return copiedAllPlayers.map(player => new Player(player));
    }
}

export default Game;