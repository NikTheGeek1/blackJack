import AnimationUtils from './DealingCardAnimationUtils';
import DynamicManager from '../coordinates_sizes/DynamicManager';

class DealingCards {
    constructor(canvasManager, onFinishCb) {
        this.onFinishCb = onFinishCb;
        this.animationUtils = new AnimationUtils(canvasManager.game);
        this.canvasManager = canvasManager;
    }

    start() {
        this._dealingCardRecursive();
    }

    _initialValues() {
        let x = DynamicManager.constants.DEALING_CARD_INITIAL_COORDS.x;
        let y = DynamicManager.constants.DEALING_CARD_INITIAL_COORDS.y;
        const playerIdx = this.animationUtils.nextFramePlayerIdx;
        const cardIdx = this.animationUtils.nextFramePlayerCardIdx;
        const allCardCoords = this.canvasManager.dynamicSizesManager.getCoordsForDealingCard(playerIdx, cardIdx);
        const cardAngle = allCardCoords.angle;
        return { x, y, allCardCoords, cardAngle };
    }

    _dealingCardRecursive() {
        let { x, y, allCardCoords, cardAngle } = this._initialValues();
        const cardInterval = setInterval(() => {
            if (!this.animationUtils.animationFinished) {
                this._persistFrame();
                this.canvasManager._drawCard(x, y, cardAngle, null, true);
                y = y + allCardCoords.y;
                x = x + allCardCoords.x;
                if (y > allCardCoords.finalY) {
                    this.animationUtils.nextFrame();
                    clearInterval(cardInterval);
                    this._dealingCardRecursive();
                }
            } else {
                clearInterval(cardInterval);
                this.canvasManager.drawAll(true, false);
                this.onFinishCb();
            }
        }, DynamicManager.constants.CARD_INTERVAL);
    }

    _persistFrame() {
        this.canvasManager.updateGame(this.animationUtils.currentFrame);
        this.canvasManager.drawAll(true, false);
    }

}



export default DealingCards;