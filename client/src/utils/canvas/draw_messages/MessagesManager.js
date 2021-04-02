import PlayerStatus from '../../../constants/PlayerStatus';
import Constants from '../../../constants/canvas/Constants';
import TokenUtils from '../TokenUtils';

class MessagesManager {
    constructor(canvasManager) {
        this.canvasManager = canvasManager;
        this.allPlayersDealerFirst = canvasManager.game.allPlayersDealerFirst;
        this.canvasContext = canvasManager.canvasContext;
    }

    drawMessagesDecider() {
        if (this.allPlayersDealerFirst.length === 1) {
            this._drawPleaseWaitForAnotherPlayer();
        }
        if (this.allPlayersDealerFirst.length > 1 && this.allPlayersDealerFirst.every(player => player.status === PlayerStatus.WAITING_GAME)) {
            if (this.canvasManager.thisPlayer.isDealer) {
                this._drawStartTheGameButton();
            } else {
                this._drawWaitForDealerToSartTheGame();
            }
        }
        if (this._isReadyToBet()) {
            this._drawBetButton();
        }

        if (this._isThisPlayerPlaying()) {
            this._drawDrawButton();
            this._drawStickButton();
        }
    }

    _isThisPlayerPlaying() {
        return this.canvasManager.thisPlayer.status === PlayerStatus.PLAYING;
    }

    _isReadyToBet() {
        return TokenUtils.tokensToMoney(this.canvasManager.thisPlayer.betTokens) && 
            this.canvasManager.thisPlayer.status === PlayerStatus.BETTING;
    }

    _drawWaitForDealerToSartTheGame() {
        this.canvasContext.font = '48px serif';
        this.canvasContext.fillText('Wait for dealer to start the game', 150, 400);
    }

    _drawStartTheGameButton() {
        this.canvasContext.font = '48px serif';
        this.canvasContext.fillText('Start game', Constants.START_BUTTON_COORDS.x, Constants.START_BUTTON_COORDS.y);
    }

    _drawPleaseWaitForAnotherPlayer() {
        this.canvasContext.font = '48px serif';
        this.canvasContext.fillText('Please wait for another player to join', 150, 400);
    }

    _drawBetButton() {
        this.canvasContext.font = '48px serif';
        this.canvasContext.fillText('BET!', Constants.BET_BUTTON_COORDS.x, Constants.BET_BUTTON_COORDS.y);
    }

    _drawDrawButton() {
        this.canvasContext.font = '48px serif';
        this.canvasContext.fillText('DRAW!', Constants.DRAW_BUTTON_COORDS.x, Constants.DRAW_BUTTON_COORDS.y);
    }

    _drawStickButton() {
        this.canvasContext.font = '48px serif';
        this.canvasContext.fillText('STICK!', Constants.STICK_BUTTON_COORDS.x, Constants.STICK_BUTTON_COORDS.y);
    }

}

export default MessagesManager;