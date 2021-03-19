import CanvasImgNames from '../../constants/CanvasImgNames';
import CanvasDynamicSizesManager from '../../utils/CanvasDynamicManager';
import RevealedCardUtils from '../../utils/RevealedCardUtils';

class CanvasManager {
    constructor(canvas, screenDims, imgsArray) {
        this.screenDims = screenDims;
        this.dynamicSizesManager = new CanvasDynamicSizesManager(screenDims);
        this.canvas = canvas;
        this.mousePos = { x: 0, y: 0 };
        this.canvasContext = canvas.getContext('2d');
        this.imgsArray = imgsArray.map(img => ({ img: new Image(), src: img.src, name: img.name }));
        this.imgsToLoadCount = imgsArray.length;
        this.game = null;
    }

    _findImageToDraw(imgName) {
        return this.imgsArray.filter(img => img.name === imgName)[0];
    }

    _beginLoadingImage(imageObj) {
        imageObj.img.onload = (image) => this._loadImgsAndStartIfReady(image);
        imageObj.img.src = imageObj.src;
    }

    _loadImgsAndStartIfReady() {
        this.imgsToLoadCount--;
        if (this.imgsToLoadCount === 0) {
            this.imgsToLoadCount = this.imgsArray.length;
            this._imgLoadingDoneStart();
        }
    }

    _imgLoadingDoneStart() {
        this.drawAll();
    }

    _drawTable() {
        const table = this._findImageToDraw(CanvasImgNames.TABLE).img;
        const tableCoords = this.dynamicSizesManager.TABLE_COORDS;
        this.canvasContext.drawImage(table, tableCoords.x, tableCoords.y);
    }

    _drawPosition(playerPosition) {
        const positionImgObj = this._findImageToDraw(CanvasImgNames.POSITION);
        const positionCoords = this.dynamicSizesManager.POSISITIONS_COORDS[playerPosition];
        if (!positionCoords) return;
        this.canvasContext.drawImage(positionImgObj.img, positionCoords.x, positionCoords.y);
    }


    _drawPositions() {
        for (let i = 0; i < this.game.allPlayersDealerFirst.length; i++) {
            this._drawPosition(i);
        }
    }


    _drawCard(playerIdx, cardIdx, card) {
        let cardImgObj = this._findImageToDraw(CanvasImgNames.CARD_BACK_BLUE);
        if (card) {
            const imgName = RevealedCardUtils.getCardImgName(card);
            cardImgObj = this._findImageToDraw(imgName);
        }
        const cardCoords = this.dynamicSizesManager.CARD_COORDS(playerIdx, cardIdx);
        const cardSize = CanvasDynamicSizesManager.originalSizes.CARD;
        // rotate the canvas to the specified degrees
        this.canvasContext.save();
        this.canvasContext.translate(cardCoords.x, cardCoords.y);
        this.canvasContext.rotate(CanvasDynamicSizesManager.constants.CARD_NUM_OFFSETS[cardIdx].angle * Math.PI / 180);

        this.canvasContext.drawImage(cardImgObj.img,
            0,
            0,
            cardSize.width,
            cardSize.height
        );

        // we’re done with the rotating so restore the unrotated context
        // this.canvasContext.translate(-cardCoords.x, -cardCoords.y);
        // this.canvasContext.rotate(-(cardIdx + 4) * Math.PI / 180);
        this.canvasContext.restore();

    }

    _drawCards() {
        for (let playerIdx = 0; playerIdx < this.game.allPlayersDealerFirst.length; playerIdx++) {
            const player = this.game.allPlayersDealerFirst[playerIdx];
            for (let cardIdx = 0; cardIdx < player.displayedCards.length; cardIdx++) {

                const card = player.displayedCards[cardIdx];
                if (card.visibility === "HIDDEN") { // TODO: transform this to enum
                    this._drawCard(playerIdx, cardIdx);
                } else {
                    this._drawCard(playerIdx, cardIdx, card);
                }
            }
        }
    }

    drawAll(screenDimensions) {
        if (screenDimensions) {
            this.screenDims = screenDimensions;
            this.dynamicSizesManager = new CanvasDynamicSizesManager(screenDimensions);
        }
        this.canvasContext.save();
        this.canvasContext.scale(
            this.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR,
            this.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR
        );

        this._drawTable();
        if (!this.game) {
            this.canvasContext.restore();
            return;
        }
        this._drawPositions();
        this._drawCards();
        this.canvasContext.restore();
    }

    _cardAnimation() {
        let x = 500;
        let y = 0;
        const cardInterval = setInterval(() => {
            this.drawAll();
            this.canvasContext.save();
            this.canvasContext.scale(
                this.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR,
                this.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR
            );
            let cardImgObj = this._findImageToDraw(CanvasImgNames.CARD_BACK_BLUE);
            const cardSize = CanvasDynamicSizesManager.originalSizes.CARD;

            this.canvasContext.drawImage(cardImgObj.img,
                x,
                y,
                cardSize.width,
                cardSize.height
            );
            y = y + 10;
            x = x + 2;
            this.canvasContext.restore();
            if (y > 400) clearInterval(cardInterval);
        }, 10);


    }




    loadImagesAndStart(screenDims) {
        this.screenDims = screenDims;
        for (const image of this.imgsArray) {
            this._beginLoadingImage(image);
        }
    }

    updateDrawing(game) {
        this.game = game;
        this.drawAll();
    }

    updateMousePos(x, y) {
        this.mousePos.x = x;
        this.mousePos.y = y;
    }
}


export default CanvasManager;