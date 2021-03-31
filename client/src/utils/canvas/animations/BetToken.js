import CanvasDynamicSizesManager from '../coordinates_sizes/DynamicManager';


class BetToken {
    constructor(canvasManager, betAmount) {
        this.canvasManager = canvasManager;
        this.betAmount = betAmount;
        this.thisPlayerIdxJustPlayers = null;
        this.thisPlayerIdxAllPlayers = null;
        this.tokenColumnIdx = null;
        this._setPlayerIdx();
        this._setTokenColumnIdx();
        this.onFinishCb = () => { };
        this.backupCanvas = null;
        this.data = this._initialValues();
    }

    _setTokenColumnIdx() {
        const tokens = this.canvasManager.thisPlayer.betTokens;
        this.tokenColumnIdx = Object.keys(tokens).findIndex(tokenKey => tokenKey === this.betAmount);
    }

    _setPlayerIdx() {
        this.thisPlayerIdxAllPlayers = this.canvasManager.game.allPlayersDealerFirst.findIndex(player => player.email === this.canvasManager.thisPlayer.email);
        this.thisPlayerIdxJustPlayers = this.canvasManager.game.players.findIndex(player => player.email === this.canvasManager.thisPlayer.email);
    }

    playAnimation() {
        this._subtractBetTokensFromMoneyTokens();
        this.canvasManager.drawAll(true, true);
        this.drawCanvasStateToBackupCanvas();
        this._drawTokenRecursively();
    }

    _subtractBetTokensFromMoneyTokens() {
        this.canvasManager.thisPlayer.tokens[this.betAmount] -= 1;
        this.canvasManager.game.allPlayersDealerFirst[this.thisPlayerIdxAllPlayers].tokens[this.betAmount] -= 1;
        this.canvasManager.game.players[this.thisPlayerIdxJustPlayers].tokens[this.betAmount] -= 1;
    }

    _increaseBetTokens() {
        this.canvasManager.thisPlayer.betTokens[this.betAmount] += 1;
        this.canvasManager.game.allPlayersDealerFirst[this.thisPlayerIdxAllPlayers].betTokens[[this.betAmount]] += 1;
        this.canvasManager.game.players[this.thisPlayerIdxJustPlayers].betTokens[[this.betAmount]] += 1;
    }

    _drawTokenRecursively() {
        this.drawBackupCanvasStateToCanvas(false);
        const shrinkedSize = CanvasDynamicSizesManager.originalSizes.TOKEN.width - this.data.shrinkIncrement;
        this.canvasManager._drawToken(this.data.tokenCurrentCoords.x, this.data.tokenCurrentCoords.y, this.tokenColumnIdx, true, true, shrinkedSize);
        this._incrementCoords(this.data.tokenCurrentCoords, this.data.allTokenCoords);
        this.data.shrinkIncrement += 1; // TODO: make this constant
        if (this._shouldStopDrawingToken(this.data.tokenCurrentCoords, this.data.allTokenCoords)) {
            this._persistFrame();
            this.onFinishCb();
        } else {
            requestAnimationFrame(this._drawTokenRecursively.bind(this));
        }
    }

    _incrementCoords(currentCoords, allTokenCoords) {
        currentCoords.x += allTokenCoords.x;
        currentCoords.y += allTokenCoords.y;
    }

    _shouldStopDrawingToken(currentCoords, allTokenCoords) {
        return currentCoords.y < allTokenCoords.finalY;
    }

    _initialValues() {
        const tokens = this.canvasManager.thisPlayer.tokens;
        const tokenIdx = tokens[this.betAmount];
        const tokenInitialCoords = this.canvasManager.dynamicSizesManager.TOKEN_COORDS(tokenIdx)[this.tokenColumnIdx];
        const shrinkIncrement = 0;
        const allTokenCoords = this.canvasManager.dynamicSizesManager.getCoordsForPlacingBetToken(this.tokenColumnIdx, tokenIdx, this.thisPlayerIdxAllPlayers);
        const tokenFinalCoords = this.canvasManager.dynamicSizesManager.BET_TOKEN_COORDS(this.thisPlayerIdxAllPlayers, tokenIdx);
        const tokenCurrentCoords = { ...tokenInitialCoords };
        return { tokenCurrentCoords, allTokenCoords, tokenFinalCoords, shrinkIncrement };
    }

    _persistFrame() {
        this._increaseBetTokens();
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