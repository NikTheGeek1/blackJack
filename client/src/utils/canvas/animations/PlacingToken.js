import AnimationUtils from './PlaceTokensAnimationUtils';
import CanvasDynamicSizesManager from '../coordinates_sizes/DynamicManager';

class PlacingTokens {
    constructor(canvasManager, onFinishCb) {
        this.onFinishCb = onFinishCb;
        this.animationUtils = new AnimationUtils(canvasManager.thisPlayer);
        this.canvasManager = canvasManager;
    }

    _configureTokenSpeedAndDistanceBasedOnNumberOfTokens() {
        // TODO: Change the speed and distance of the simulation
        // speed: time delay in the timeInterval function. 
        // the more the number of tokens, the less the speed ( make it constant first)
        // distance: distance of tokens between frames
        // the more the tokens the more the distance  ( in dynamicCoord class )
    }

    start() {
        this._drawTokensRecursively();
    }

    _initialValues() {
        let x1, x2, x3, x4, x5, y1, y2, y3, y4, y5;
        const tokensInitialCoords = CanvasDynamicSizesManager.constants.TOKENS_ANIMATION_INITIAL_COORDS;
        x1 = x2 = x3 = x4 = x5 = tokensInitialCoords.x;
        y1 = y2 = y3 = y4 = y5 = tokensInitialCoords.y;
        // TODO: MAKE THESE CONSTANTS
        y1 -= 140;
        y2 -= 260;
        y3 -= 380;
        y4 -= 500;

        const tokenIdx = this.animationUtils.nextFrameTokenIdx;
        const allTokenCoords = this.canvasManager.dynamicSizesManager.getCoordsForPlacingToken(tokenIdx);
        const tokensFinalCoords = this.canvasManager.dynamicSizesManager.TOKEN_COORDS(tokenIdx);
        const tokensCurrentCoords = [
            { x: x1, y: y1 },
            { x: x2, y: y2 },
            { x: x3, y: y3 },
            { x: x4, y: y4 },
            { x: x5, y: y5 }
        ]
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
                this.canvasManager.drawAll(true, true);
                const shouldDrawToken = this._shouldDrawTokensArray(tokensCurrentCoords, allTokenCoords);
                this._drawMovementOrPlaceTokenAtFinalPosition(shouldDrawToken, tokensCurrentCoords, tokensFinalCoords, allTokenCoords);
                this._incrementCoords(tokensCurrentCoords, allTokenCoords);
                if (this._shouldDrawNextBatchOfTokens(shouldDrawToken)) {
                    this.animationUtils.nextFrame();
                    this._persistFrame();
                    this._drawTokensRecursively();
                    clearInterval(tokenInterval);
                }
            } else {
                clearInterval(tokenInterval);
                this.onFinishCb();
            }
        }, 80);
    }

    _persistFrame() {
        this.canvasManager.updateThisPlayer(this.animationUtils.currentFrame);
        this.canvasManager.drawAll(true, true);
    }


}



export default PlacingTokens;