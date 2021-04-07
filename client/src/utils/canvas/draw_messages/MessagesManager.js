import PlayerStatus from '../../../constants/PlayerStatus';
import Constants from '../../../constants/canvas/Constants';
import TokenUtils from '../TokenUtils';
import CanvasDynamicSizesManager from '../coordinates_sizes/DynamicManager';

class MessagesManager {
    static anotherMessageIsDisplayed = false;
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
        if (this._isThisPlayerPlaying() && !MessagesManager.anotherMessageIsDisplayed) {
            this._drawDrawButton();
            this._drawStickButton();
        }

        if (this.canvasManager.thisPlayer.status === PlayerStatus.BETTING &&
            this.canvasManager.initialAnimationFinished) {
            this._drawTimeToBet();
        }
    }

    _isThisPlayerPlaying() {
        return this.canvasManager.thisPlayer.status === PlayerStatus.PLAYING;
    }

    _isReadyToBet() {
        return TokenUtils.tokensToMoney(this.canvasManager.thisPlayer.betTokens) && 
            this.canvasManager.thisPlayer.status === PlayerStatus.BETTING;
    }

    _drawTimeToBet() {
        const x = Constants.BET_ARROW_COORDS.x + Constants.BET_MESSAGE_OFFSETS.x;
        const y = Constants.BET_ARROW_COORDS.y + Constants.BET_MESSAGE_OFFSETS.y;
        this.canvasContext.font = Constants.TIME_TO_BET_FONT;
        this.canvasContext.fillStyle = 'black';
        this.canvasContext.save();
        this.canvasContext.translate(x, y);
        this.canvasContext.rotate(Constants.BET_MESSAGE_OFFSETS.angle * Math.PI / 180);
        this.canvasContext.fillText('Time to bet!', 0, 0);
        this.canvasContext.restore();
    }


    _drawWaitForDealerToSartTheGame() {
        this.canvasContext.font = Constants.MESSAGES_FONT;
        this.canvasContext.fillText('Wait for dealer to start the game', 150, 400);
    }

    _drawStartTheGameButton() {
        this.canvasContext.font = Constants.MESSAGES_FONT;
        this.canvasContext.fillText('Start game', Constants.START_BUTTON_COORDS.x, Constants.START_BUTTON_COORDS.y);
    }

    _drawPleaseWaitForAnotherPlayer() {
        this.canvasContext.font = Constants.MESSAGES_FONT;
        this.canvasContext.fillText('Please wait for another player to join', 150, 400);
    }

    _drawBetButton() {
        this.canvasContext.font = Constants.MESSAGES_FONT;
        this.canvasContext.fillText('BET!', Constants.BET_BUTTON_COORDS.x, Constants.BET_BUTTON_COORDS.y);
    }

    _drawDrawButton() {
        this.canvasContext.font = Constants.MESSAGES_FONT;
        this.canvasContext.fillText('DRAW!', Constants.DRAW_BUTTON_COORDS.x, Constants.DRAW_BUTTON_COORDS.y);
    }

    _drawStickButton() {
        this.canvasContext.font = Constants.MESSAGES_FONT;
        this.canvasContext.fillText('STICK!', Constants.STICK_BUTTON_COORDS.x, Constants.STICK_BUTTON_COORDS.y);
    }

    static drawBusted(canvasManager, onFinishCb) {
        canvasManager.canvasContext.save();
        canvasManager.canvasContext.scale(
            canvasManager.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR,
            canvasManager.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR
        );
      
        canvasManager.canvasContext.font = Constants.BUSTED_FONT;
        canvasManager.canvasContext.fillText('BUSTED!', Constants.BUSTED_MSG_COORDS.x, Constants.BUSTED_MSG_COORDS.y);
        canvasManager.canvasContext.restore();
        const timeOut = setTimeout(() => {
            onFinishCb();
            MessagesManager.anotherMessageIsDisplayed = false;
            clearTimeout(timeOut);
        }, 2000);
    }

    static drawBJ(canvasManager, onFinishCb) {
        canvasManager.drawAll(true, true); // we need this to remove the STICK/DRAW message
        canvasManager.canvasContext.save();
        canvasManager.canvasContext.scale(
            canvasManager.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR,
            canvasManager.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR
        );
        canvasManager.canvasContext.font = Constants.BJ_FONT;
        canvasManager.canvasContext.fillText('BLACK JACK!!!', Constants.BJ_MSG_COORDS.x, Constants.BJ_MSG_COORDS.y);  
        canvasManager.canvasContext.restore();
        const timeOut = setTimeout(() => {
            onFinishCb();
            MessagesManager.anotherMessageIsDisplayed = false;
            clearTimeout(timeOut);
        }, 2000);
    }

    static drawVerdict(canvasManager, verdict, onFinishCb, verdictSlideAnimation) {
        canvasManager.drawAll(true, true); // we need this to remove the STICK/DRAW message
        canvasManager.canvasContext.save();
        canvasManager.canvasContext.scale(
            canvasManager.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR,
            canvasManager.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR
        );
        canvasManager.canvasContext.font = Constants.VERDICT_FONT;
        if (verdict === "WON") {
            canvasManager.canvasContext.fillText('YOU WON!', Constants.VERDICT_COORDS.x, Constants.VERDICT_COORDS.y);  
            
        } else if (verdict === "LOST") {
            canvasManager.canvasContext.fillText('YOU LOST!', Constants.VERDICT_COORDS.x, Constants.VERDICT_COORDS.y);  
        }
        
        canvasManager.canvasContext.restore();
        verdictSlideAnimation();
        const timeOut = setTimeout(() => {
            onFinishCb();
            MessagesManager.anotherMessageIsDisplayed = false;
            clearTimeout(timeOut);
        }, 2000);
    }

    static drawDealersLastStatus(canvasManager, dealerStatus, onFinishCb, verdictSlideAnimation) {
        canvasManager.canvasContext.save();
        canvasManager.canvasContext.scale(
            canvasManager.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR,
            canvasManager.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR
        );
        if (dealerStatus === PlayerStatus.BLACKJACK) {
            canvasManager.canvasContext.font = Constants.BJ_FONT;
            canvasManager.canvasContext.fillText('BLACK JACK!', Constants.BJ_MSG_COORDS.x, Constants.BJ_MSG_COORDS.y);  
            
        } else if (dealerStatus === PlayerStatus.BUSTED) {
            canvasManager.canvasContext.font = Constants.BUSTED_FONT;
            canvasManager.canvasContext.fillText('BUSTED!', Constants.BUSTED_MSG_COORDS.x, Constants.BUSTED_MSG_COORDS.y);  
        }
        canvasManager.canvasContext.restore();
        verdictSlideAnimation();
        const timeOut = setTimeout(() => {
            onFinishCb();
            MessagesManager.anotherMessageIsDisplayed = false;
            clearTimeout(timeOut);
        }, 2000);
    }

}

export default MessagesManager;