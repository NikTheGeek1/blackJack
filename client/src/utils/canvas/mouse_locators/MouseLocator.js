import TokensLocator from './TokensLocator';
import CardLocator from './CardLocator';

class MouseLocator {

    constructor(screenDims, mousePos, thisPlayer, game) {
        this.screenDims = screenDims;
        this.mousePos = mousePos;
        this.game = game;
        this.thisPlayer = null;
        this._setThisPlayer(thisPlayer.email);
    }

    analyseMouseLocation() {
        const onTokens = new TokensLocator(this.mousePos, this.screenDims, this.thisPlayer).mouseOnTokens();
        const onCards = new CardLocator(this.mousePos, this.screenDims, this.game).mouseOnCards();

        return onTokens || onCards;
    }

    _setThisPlayer(thisPlayerEmail) {
        this.thisPlayer = this.game.allPlayersDealerFirst.filter(player => player.email === thisPlayerEmail)[0];
    }




    
}


export default MouseLocator;