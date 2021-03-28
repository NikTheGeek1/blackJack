import AnimationUtils from './PlaceTokensAnimationUtils';
import CanvasDynamicSizesManager from '../coordinates_sizes/DynamicManager';

class PlacingTokens {
    constructor(canvasManager, onFinishCb) {
        this.onFinishCb = onFinishCb;
        this.animationUtils = new AnimationUtils(canvasManager.thisPlayer);
        this.canvasManager = canvasManager;
        this.backupCanvas = null;
    }

    start() {
        this.drawCanvasStateToBackupCanvas();
        this._drawTokensRecursively();
    }

    _initialValues() {
        const tokensInitialCoords = CanvasDynamicSizesManager.constants.TOKENS_ANIMATION_INITIAL_COORDS;

        const tokenIdx = this.animationUtils.nextFrameTokenIdx;
        const allTokenCoords = this.canvasManager.dynamicSizesManager.getCoordsForPlacingToken(tokenIdx);
        const tokensFinalCoords = this.canvasManager.dynamicSizesManager.TOKEN_COORDS(tokenIdx);
        const tokensCurrentCoords = [
            { ...tokensInitialCoords[0]},
            { ...tokensInitialCoords[1]},
            { ...tokensInitialCoords[2]},
            { ...tokensInitialCoords[3]},
            { ...tokensInitialCoords[4]},
            { ...tokensInitialCoords[5]},
        ];
        return { tokensCurrentCoords, allTokenCoords, tokensFinalCoords };
    }

    _shouldDrawTokensArray(currentCoords, allTokenCoords) {
        const shouldDrawToken = allTokenCoords.map((token, i) => currentCoords[i].y < token.finalY && this.animationUtils.shouldDrawToken[i]);
        return shouldDrawToken;
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

    _incrementCoords(currentCoords, allTokenCoords) {
        for (let i = 0; i < currentCoords.length; i++) {
            currentCoords[i].x += allTokenCoords[i].x;
            currentCoords[i].y += allTokenCoords[i].y;
        }
    }

    _shouldDrawNextBatchOfTokens(shouldDrawTokenArray) {
        return shouldDrawTokenArray.every(_ => !_);
    }

    _drawTokensRecursively() {
        let { tokensCurrentCoords, allTokenCoords, tokensFinalCoords } = this._initialValues();
        const tokenInterval = setInterval(() => {
            if (!this.animationUtils.animationFinished) {
                this.drawBackupCanvasStateToCanvas(false);
                const shouldDrawToken = this._shouldDrawTokensArray(tokensCurrentCoords, allTokenCoords);
                this._drawMovementOrPlaceTokenAtFinalPosition(shouldDrawToken, tokensCurrentCoords, tokensFinalCoords, allTokenCoords);
                this._incrementCoords(tokensCurrentCoords, allTokenCoords);
                if (this._shouldDrawNextBatchOfTokens(shouldDrawToken)) {
                    this.animationUtils.nextFrame();
                    this._persistFrame();
                    this.drawCanvasStateToBackupCanvas();
                    this._drawTokensRecursively();
                    clearInterval(tokenInterval);
                }
            } else {
                clearInterval(tokenInterval);
                this.onFinishCb();
            }
        }, 10); // TODO: Make this constant 
    }

    _persistFrame() {
        this.canvasManager.updateThisPlayer(this.animationUtils.currentFrame);
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



export default PlacingTokens;