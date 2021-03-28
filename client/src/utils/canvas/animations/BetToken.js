import AnimationUtils from './PlaceTokensAnimationUtils';
import TokenUtils from '../TokenUtils';
import CanvasDynamicSizesManager from '../coordinates_sizes/DynamicManager';


class BetToken {
    constructor(canvasManager, betAmount) {
        this.canvasManager = canvasManager;
        this.betAmount = betAmount;
        this.thisPlayerIdx = null;
        this.tokenColumnIdx = null;
        this._setPlayerIdx();
        this._setTokenColumnIdx();
        this.onFinishCb = () => { };
    }

    _setTokenColumnIdx() {
        const tokens = TokenUtils.moneyToTokens(this.canvasManager.thisPlayer.money);
        this.tokenColumnIdx = Object.keys(tokens).findIndex(tokenKey => tokenKey === this.betAmount);
    }

    _setPlayerIdx() {
        this.thisPlayerIdx = this.canvasManager.game.allPlayersDealerFirst.findIndex(player => player.email === this.canvasManager.thisPlayer.email);
    }

    playAnimation() {
        this._subtractBetFromMoney();
        this.canvasManager.drawAll(true, true);
        this.drawCanvasStateToBackupCanvas();
        this._drawTokenRecursively();
    }

    _subtractBetFromMoney() {
        this.canvasManager.thisPlayer.money -= +this.betAmount;
        this.canvasManager.game.allPlayersDealerFirst[this.thisPlayerIdx].money -= +this.betAmount;
    }

    _increaseBet() {
        this.canvasManager.thisPlayer.bet += +this.betAmount;
        this.canvasManager.game.allPlayersDealerFirst[this.thisPlayerIdx].bet += +this.betAmount;
    }

    _drawTokenRecursively() {
        let { tokenCurrentCoords, allTokenCoords } = this._initialValues();
        let shrinkIncrement = 0;
        const tokenInterval = setInterval(() => {
            this.drawBackupCanvasStateToCanvas(false);
            const shrinkedSize = CanvasDynamicSizesManager.originalSizes.TOKEN.width - shrinkIncrement;
            this.canvasManager._drawToken(tokenCurrentCoords.x, tokenCurrentCoords.y, this.tokenColumnIdx, true, true, shrinkedSize);
            this._incrementCoords(tokenCurrentCoords, allTokenCoords);
            if (this._shouldStopDrawingToken(tokenCurrentCoords, allTokenCoords)) {
                this._persistFrame();
                clearInterval(tokenInterval);
                this.onFinishCb();
            }
            shrinkIncrement += 1; // TODO: make this constant
        }, 5); // TODO: Make this constant 
    }

    _incrementCoords(currentCoords, allTokenCoords) {
            currentCoords.x += allTokenCoords.x;
            currentCoords.y += allTokenCoords.y;
    }

    _shouldStopDrawingToken(currentCoords, allTokenCoords) {
        return currentCoords.y < allTokenCoords.finalY;
    }

    _drawMovementOrPlaceTokenAtFinalPosition(shouldDrawToken, currentCoords, tokensFinalCoords, allTokenCoords) {
        for (let i = 0; i < currentCoords.length; i++) {
            if (shouldDrawToken[i]) {
                this.canvasManager._drawToken(currentCoords[i].x, currentCoords[i].y, i, true);
            } else if (currentCoords[i].y >= allTokenCoords[i].finalY && this.animationUtils.shouldDrawToken[i]) {
                this.canvasManager._drawToken(tokensFinalCoords[i].x, tokensFinalCoords[i].y, i, true);
            }
        }
    }

    _initialValues() {
        const tokens = TokenUtils.moneyToTokens(this.canvasManager.thisPlayer.money);
        const tokenIdx = tokens[this.betAmount];
        const tokenInitialCoords = this.canvasManager.dynamicSizesManager.TOKEN_COORDS(tokenIdx)[this.tokenColumnIdx];

        const allTokenCoords = this.canvasManager.dynamicSizesManager.getCoordsForPlacingBetToken(this.tokenColumnIdx, tokenIdx, this.thisPlayerIdx);
        const tokenFinalCoords = this.canvasManager.dynamicSizesManager.BET_TOKEN_COORS(this.thisPlayerIdx, tokenIdx);
        const tokenCurrentCoords = { ...tokenInitialCoords };
        return { tokenCurrentCoords, allTokenCoords, tokenFinalCoords };
    }

    _persistFrame() {
        this._increaseBet();
        this.canvasManager.drawAll(true, true);
    }


    drawCanvasStateToBackupCanvas() {
        this.backupCanvas = document.createElement('canvas');
        this.backupCanvas.width = CanvasDynamicSizesManager.sizeUtils.canvasStyle(this.canvasManager.screenDims).width;
        this.backupCanvas.height = this.canvasManager.screenDims.height
        const destCtx = this.backupCanvas.getContext('2d');
        destCtx.drawImage(this.canvasManager.canvas, 0, 0);
        this.isBackupCanvasDrawn = true;
    }

    drawBackupCanvasStateToCanvas(shouldClearBackupCanvas) {
        const destCtx = this.canvasManager.canvasContext;
        destCtx.drawImage(this.backupCanvas, 0, 0);
        if (shouldClearBackupCanvas) {
            this.backupCanvas.getContext('2d').clearRect(0, 0, this.backupCanvas.width, this.backupCanvas.height);
            this.isBackupCanvasDrawn = false;
        }        
    }
}


export default BetToken;