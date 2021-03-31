import AnimationUtils from './DealingCardAnimationUtils';
import DynamicManager from '../coordinates_sizes/DynamicManager';
import CanvasDynamicSizesManager from '../coordinates_sizes/DynamicManager';

class DealingCards {
    constructor(canvasManager, onFinishCb) {
        this.onFinishCb = onFinishCb;
        this.animationUtils = new AnimationUtils(canvasManager.game);
        this.canvasManager = canvasManager;
        this.backupCanvas = null;
        this.data = this._initialValues();

    }

    start() {
        this.drawCanvasStateToBackupCanvas();
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
        if (!this.animationUtils.animationFinished) {
            this.drawBackupCanvasStateToCanvas(false);
            this.canvasManager._drawCard(this.data.x, this.data.y, this.data.cardAngle, null, true);
            this.data.y = this.data.y + this.data.allCardCoords.y;
            this.data.x = this.data.x + this.data.allCardCoords.x;
            if (this.data.y > this.data.allCardCoords.finalY) {
                this.animationUtils.nextFrame();
                this._persistFrame();
                this.drawCanvasStateToBackupCanvas();
                this.data = this._initialValues();
            }
            requestAnimationFrame(this._dealingCardRecursive.bind(this));
        } else {
            this.canvasManager.drawAll(true, false);
            this.onFinishCb();
        }

    }

    _persistFrame() {
        this.canvasManager.updateGame(this.animationUtils.currentFrame);
        this.canvasManager.drawAll(true, false);
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



export default DealingCards;