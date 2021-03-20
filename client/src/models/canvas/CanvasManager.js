import CanvasImgNames from '../../constants/CanvasImgNames';
import CanvasDynamicSizesManager from '../../utils/CanvasDynamicManager';
import RevealedCardUtils from '../../utils/RevealedCardUtils';
import PlayerChoiceType from '../../models/matches/PlayerChoiceType';
import CanvasDealingCardAnimationUtils from '../../utils/CanvasDealingCardAnimationUtils';

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

    _drawCard(x, y, angle, card, shouldScale) {
        let cardImgObj = this._findImageToDraw(CanvasImgNames.CARD_BACK_BLUE);
        if (card) {
            const imgName = RevealedCardUtils.getCardImgName(card);
            cardImgObj = this._findImageToDraw(imgName);
        }
        const cardSize = CanvasDynamicSizesManager.originalSizes.CARD;
        // rotate the canvas to the specified degrees
        this.canvasContext.save();
        if (shouldScale) {
            this.canvasContext.scale(
                this.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR,
                this.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR
            );
        }
        this.canvasContext.translate(x, y);
        this.canvasContext.rotate(angle * Math.PI / 180);

        this.canvasContext.drawImage(cardImgObj.img,
            0,
            0,
            cardSize.width,
            cardSize.height
        );

        this.canvasContext.restore();

    }

    _drawCards() {
        for (let playerIdx = 0; playerIdx < this.game.allPlayersDealerFirst.length; playerIdx++) {
            const player = this.game.allPlayersDealerFirst[playerIdx];
            for (let cardIdx = 0; cardIdx < player.displayedCards.length; cardIdx++) {
                const cardCoords = this.dynamicSizesManager.CARD_COORDS(playerIdx, cardIdx);
                const cardAngle = CanvasDynamicSizesManager.constants.CARD_NUM_OFFSETS[cardIdx].angle;
                const card = player.displayedCards[cardIdx];
                if (card.visibility === "HIDDEN") { // TODO: transform this to enum
                    this._drawCard(cardCoords.x, cardCoords.y, cardAngle);
                } else {
                    this._drawCard(cardCoords.x, cardCoords.y, cardAngle, card);
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

    loadImagesAndStart(screenDims) {
        this.screenDims = screenDims;
        for (const image of this.imgsArray) {
            this._beginLoadingImage(image);
        }
    }

    updateMousePos(x, y) {
        this.mousePos.x = x;
        this.mousePos.y = y;
    }

    updateDrawing(game, playerChoice) {
        switch (playerChoice.playerChoiceType) {
            case PlayerChoiceType.GAME_STARTED_DEALING:
                this._dealingAnimation(game);
                break;
            default:
                break;
        }
    }

    _dealingCardRecursive(dealingCardsAnimationUtils) {
        let x = CanvasDynamicSizesManager.constants.DEALING_CARD_INITIAL_COORDS.x;
        let y = CanvasDynamicSizesManager.constants.DEALING_CARD_INITIAL_COORDS.y;
        const playerIdx = dealingCardsAnimationUtils.nextFramePlayerIdx;
        const cardIdx = dealingCardsAnimationUtils.nextFramePlayerCardIdx;
        const allCardCoords = this.dynamicSizesManager.getCoordsForDealingCard(playerIdx, cardIdx);
        const cardAngle = allCardCoords.angle;
        const cardInterval = setInterval(() => {
            if (!dealingCardsAnimationUtils.animationFinished) {
                this._persistFrame(dealingCardsAnimationUtils.currentFrame);
                this._drawCard(x, y, cardAngle, null, true);
                y = y + allCardCoords.y;
                x = x + allCardCoords.x;
                if (y > allCardCoords.finalY) {
                    dealingCardsAnimationUtils.nextFrame();
                    clearInterval(cardInterval);
                    this._dealingCardRecursive(dealingCardsAnimationUtils)
                }
            } else {
                clearInterval(cardInterval);
                this.drawAll();
            }
        }, CanvasDynamicSizesManager.constants.CARD_INTERVAL);
    }

    _dealingAnimation(game) {
        const dealingCardsAnimationUtils = new CanvasDealingCardAnimationUtils(game);

        this._dealingCardRecursive(dealingCardsAnimationUtils);
    }


    _persistFrame(game) {
        this.game = game;
        this.drawAll();
    }
}


export default CanvasManager;