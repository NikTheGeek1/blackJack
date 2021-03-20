import Game from '../models/matches/Game';

class CanvasDealingCardAnimationUtils {
    constructor(finalFrame) {
        this.finalFrame = finalFrame;
        this.currentFrame = this.constructEmptyGameForDealingCards();
        this.animationFinished = false;
        this.nextFramePlayerIdx = 0;
        this.nextFramePlayerCardIdx = 0;
    }
    
    constructEmptyGameForDealingCards() {
        const currentFrame = new Game(this.finalFrame, true);
        currentFrame.allPlayersDealerFirst.forEach(player => player.displayedCards = []);
        return currentFrame;
    }

    nextFrame() {
        const allPlayerCardsAdded = this._addAllPlayerCard();
        if (allPlayerCardsAdded) this.animationFinished = true;
    }

    _updatePlayersHaveSameNumOfCards(currentPlayer, playersHaveSameNumOfCards) {
        const finalFramePlayerCardsLen = this.currentFrame.allPlayersDealerFirst[currentPlayer].displayedCards.length;
        const currentFramePlayerCardsLen = this.finalFrame.allPlayersDealerFirst[currentPlayer].displayedCards.length;
        playersHaveSameNumOfCards[currentPlayer] = finalFramePlayerCardsLen === currentFramePlayerCardsLen;
        return playersHaveSameNumOfCards[currentPlayer];
    }

    _updateNextFramePlayer(currentPlayerIdx, currentPlayerCardIdx) {
        const finalPlayerCardLength = this.finalFrame.allPlayersDealerFirst[currentPlayerIdx].displayedCards.length;
        const currentPlayerCardLength = this.currentFrame.allPlayersDealerFirst[currentPlayerIdx].displayedCards.length;
        if (finalPlayerCardLength === currentPlayerCardLength && this.finalFrame.allPlayersDealerFirst.length === currentPlayerIdx + 1) return;
        this.nextFramePlayerCardIdx = (finalPlayerCardLength === currentPlayerCardLength) ? 0 : currentPlayerCardIdx + 1;
        this.nextFramePlayerIdx = !!!this.nextFramePlayerCardIdx ? currentPlayerIdx + 1 : currentPlayerIdx;
    }

    _addAllPlayerCard() {
        const playersHaveSameNumOfCards = this.finalFrame.allPlayersDealerFirst.map(_ => false);
        for(let p=0; p<this.finalFrame.allPlayersDealerFirst.length; p++) {
            if (!this._updatePlayersHaveSameNumOfCards(p, playersHaveSameNumOfCards)) {
                const missingAllPlayerCardIdx = this.currentFrame.allPlayersDealerFirst[p].displayedCards.length;
                const missingAllPlayerCard = this.finalFrame.allPlayersDealerFirst[p].displayedCards[missingAllPlayerCardIdx];
                this.currentFrame.allPlayersDealerFirst[p].displayedCards.push(missingAllPlayerCard);
                this._updatePlayersHaveSameNumOfCards(p, playersHaveSameNumOfCards);
                this._updateNextFramePlayer(p, missingAllPlayerCardIdx);
                break;
            }
        }
        return playersHaveSameNumOfCards.every(c => c);
    }

}

export default CanvasDealingCardAnimationUtils;