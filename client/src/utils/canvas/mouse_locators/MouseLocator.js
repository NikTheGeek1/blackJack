import TokensLocator from './TokensLocator';
import BetTokensLocator from './BetTokensLocator';
import CardLocator from './CardLocator';
import StartGameButtonLocator from './StartGameButtonLocator';
import BetButtonLocator from './BetButtonLocator';
import TokenUtils from '../TokenUtils';
import PlayerStatus from '../../../constants/PlayerStatus';
import DrawButtonLocator from './DrawButtonLocator';
import StickButtonLocator from './StickButtonLocator';
import MessagesManager from '../draw_messages/MessagesManager';

class MouseLocator {

    constructor(screenDims, mousePos, thisPlayer, game, gameType) {
        this.screenDims = screenDims;
        this.mousePos = mousePos;
        this.game = game;
        this.thisPlayer = thisPlayer;
        this.gameType = gameType;
    }

    analyseMouseLocation() {
        let onStartGameButton;
        let onTokens;
        let onBetTokens;
        let onCards;
        let onBetButton;
        let onStickButton;
        let onDrawButton;

        if (this._canThisPlayerClickOnStartGame()) {
            onStartGameButton = new StartGameButtonLocator(this.mousePos).mouseOnButton();
        }

        if (this._isClickAllowed())  {
            if (!this.thisPlayer.isDealer && this.thisPlayer.status === PlayerStatus.BETTING) {
                onTokens = new TokensLocator(this.mousePos, this.screenDims, this.thisPlayer).mouseOnTokens();
            }
            onCards = new CardLocator(this.mousePos, this.screenDims, this.game).mouseOnCards();
        }
        
        if (this.isThisPlayerReadyToBet()) {
            onBetTokens = new BetTokensLocator(this.mousePos, this.screenDims, this.thisPlayer, this.game.allPlayersDealerFirst).mouseOnBetTokens();
            onBetButton = new BetButtonLocator(this.mousePos).mouseOnBetButton();
        }

        if (this.isThisPlayerPlaying() && !MessagesManager.anotherMessageIsDisplayed) {
            onDrawButton = new DrawButtonLocator(this.mousePos).mouseOnDrawButton();
            onStickButton = new StickButtonLocator(this.mousePos).mouseOnStickButton();
        }


        return onTokens || onCards || onBetTokens || onStartGameButton || onBetButton || onDrawButton || onStickButton;
    }

    isThisPlayerPlaying() {
        return this.thisPlayer.status === PlayerStatus.PLAYING;
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
        return !this._hasGameStarted() && 
        ((this.thisPlayer.isDealer && this.game.allPlayersDealerFirst.length > 1 && this.gameType === "HUMANS") ||
        (this.gameType === "COMPUTER" && this._isThisPlayerAfterDealer()));
    }

    _isThisPlayerAfterDealer() {
        return this.game.allPlayersDealerFirst[1].email === this.thisPlayer.email;
    }
}


export default MouseLocator;