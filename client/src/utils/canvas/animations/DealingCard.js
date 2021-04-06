import DynamicManager from '../coordinates_sizes/DynamicManager';
import CanvasDynamicSizesManager from '../coordinates_sizes/DynamicManager';

class DealingCards {
    constructor(canvasManager, playingPlayerEmail, onFinishCb) {
        this.onFinishCb = onFinishCb;
        this.canvasManager = canvasManager;
        this.backupCanvas = null;
        this.playingPlayerIdx = this._getPlayingPlayerIdx(playingPlayerEmail);
        this.data = this._initialValues();
    }

    _getPlayingPlayerIdx(playingPlayerEmail) {
        for (let playerIdx = 0; playerIdx < this.canvasManager.game.allPlayersDealerFirst.length; playerIdx++) {
            const player = this.canvasManager.game.allPlayersDealerFirst[playerIdx];
            if (player.email === playingPlayerEmail) {
                return playerIdx;
            }
        }
    }

    start() {
        this.drawCanvasStateToBackupCanvas();
        this._dealingCardRecursive();
    }

    _initialValues() {
        let x = DynamicManager.constants.DEALING_CARD_INITIAL_COORDS.x;
        let y = DynamicManager.constants.DEALING_CARD_INITIAL_COORDS.y;
        let cardIdx;
        cardIdx = this.canvasManager.game.allPlayersDealerFirst[this.playingPlayerIdx].displayedCards.length - 1;
        const allCardCoords = this.canvasManager.dynamicSizesManager.getCoordsForDealingCard(this.playingPlayerIdx, cardIdx);
        const cardAngle = allCardCoords.angle;
        return { x, y, allCardCoords, cardAngle };
    }

    _dealingCardRecursive() {
        this.drawBackupCanvasStateToCanvas(false);
        this.canvasManager._drawCard(this.data.x, this.data.y, this.data.cardAngle, null, true);
        this.data.y = this.data.y + this.data.allCardCoords.y;
        this.data.x = this.data.x + this.data.allCardCoords.x;
        if (this.data.y > this.data.allCardCoords.finalY) {
            this._persistFrame();
            this.onFinishCb();
            this.drawCanvasStateToBackupCanvas();
        } else {
            requestAnimationFrame(this._dealingCardRecursive.bind(this));
        }
    }

    _persistFrame() {
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



export default DealingCards;