import TokensLocator from './TokensLocator';
import BetTokensLocator from './BetTokensLocator';
import CardLocator from './CardLocator';

class MouseLocator {

    constructor(screenDims, mousePos, thisPlayer, game) {
        this.screenDims = screenDims;
        this.mousePos = mousePos;
        this.game = game;
        this.thisPlayer = thisPlayer;
    }

    analyseMouseLocation() {
        const onTokens = new TokensLocator(this.mousePos, this.screenDims, this.thisPlayer).mouseOnTokens();
        const onBetTokens = new BetTokensLocator(this.mousePos, this.screenDims, this.thisPlayer, this.game.allPlayersDealerFirst).mouseOnBetTokens();
        const onCards = new CardLocator(this.mousePos, this.screenDims, this.game).mouseOnCards();

        return onTokens || onCards || onBetTokens;
    }


    
}


export default MouseLocator;