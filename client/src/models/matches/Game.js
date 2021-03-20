import Dealer from './Dealer';

class Game {
    constructor(fetchedGame, deepCopyAllPlayers) {
        if (deepCopyAllPlayers) {
            this.allPlayersDealerFirst = this._deepCopyAllPlayers(fetchedGame.allPlayersDealerFirst);
        } else {
            this.allPlayersDealerFirst = fetchedGame.allPlayersDealerFirst;
        }
        this.players = fetchedGame.players;
        this.dealer = new Dealer(fetchedGame.dealer);
    }

    _deepCopyAllPlayers(allPlayersDealerFirst) {
        const copiedAllPlayers = [];
        allPlayersDealerFirst.forEach(player => {
            const copiedCards = [];
            player.displayedCards.forEach(card => copiedCards.push(card));
            copiedAllPlayers.push({ ...player, displayedCards: copiedCards, });
        });
        return copiedAllPlayers;
    }
}

export default Game;