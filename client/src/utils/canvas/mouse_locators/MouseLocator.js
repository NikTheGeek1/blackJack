import TokensLocator from './TokensLocator';
import BetTokensLocator from './BetTokensLocator';
import CardLocator from './CardLocator';
import StartGameButtonLocator from './StartGameButtonLocator';
import BetButtonLocator from './BetButtonLocator';
import TokenUtils from '../TokenUtils';
import PlayerStatus from '../../../constants/PlayerStatus';
class MouseLocator {

    constructor(screenDims, mousePos, thisPlayer, game) {
        this.screenDims = screenDims;
        this.mousePos = mousePos;
        this.game = game;
        this.thisPlayer = thisPlayer;
    }

    analyseMouseLocation() {
        let onStartGameButton;
        let onTokens;
        let onBetTokens;
        let onCards;
        let onBetButton;
        if (this._canThisPlayerClickOnStartGame()) {
            onStartGameButton = new StartGameButtonLocator(this.mousePos).mouseOnButton();
        }

        if (this._isClickAllowed())  {
            if (!this.thisPlayer.isDealer) {
                onTokens = new TokensLocator(this.mousePos, this.screenDims, this.thisPlayer).mouseOnTokens();
            }
            onCards = new CardLocator(this.mousePos, this.screenDims, this.game).mouseOnCards();
        }
        
        if (this.isThisPlayerReadyToBet()) {
            onBetTokens = new BetTokensLocator(this.mousePos, this.screenDims, this.thisPlayer, this.game.allPlayersDealerFirst).mouseOnBetTokens();
            onBetButton = new BetButtonLocator(this.mousePos).mouseOnBetButton();
        }

        return onTokens || onCards || onBetTokens || onStartGameButton || onBetButton;
    }

    isThisPlayerReadyToBet() {
        return TokenUtils.tokensToMoney(this.thisPlayer.betTokens) && 
            this.thisPlayer.status === PlayerStatus.BETTING;
    }

    _isClickAllowed() {
        return this._hasGameStarted();
    }


    _hasGameStarted() {
        return this.game.allPlayersDealerFirst.some(player => player.displayedCards.length);
    }

    _canThisPlayerClickOnStartGame() {
        return !this._hasGameStarted() && this.thisPlayer.isDealer && this.game.allPlayersDealerFirst.length > 1;
    }


    
}


export default MouseLocator;