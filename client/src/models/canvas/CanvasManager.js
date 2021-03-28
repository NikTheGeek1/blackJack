import CanvasImgNames from '../../constants/canvas/ImgNames';
import CanvasDynamicSizesManager from '../../utils/canvas/coordinates_sizes/DynamicManager';
import RevealedCardUtils from '../../utils/canvas/RevealedCardUtils';
import TokenUtils from '../../utils/canvas/TokenUtils';
import HoverOverType from '../../utils/canvas/mouse_locators/HoverOverTypes';

class CanvasManager {
    constructor(canvas, screenDims, imgsArray, thisPlayer, game) {
        this.screenDims = screenDims;
        this.dynamicSizesManager = new CanvasDynamicSizesManager(screenDims);
        this.canvas = canvas;
        this.thisPlayer = thisPlayer; // TODO: test that when a change is made on this object within the CanvasManager class, the change is reflected on the original object (store)
        this.canvasContext = canvas.getContext('2d');
        this.imgsArray = imgsArray.map(img => ({ img: new Image(), src: img.src, name: img.name }));
        this.imgsToLoadCount = imgsArray.length;
        this.game = game;
        this.backupCanvas = null;
        this.isBackupCanvasDrawn = false;
    }

    updateGame(game) {
        this.game = game;
    }

    updateThisPlayer(thisPlayer) {
        this.thisPlayer = thisPlayer;
    }

    setScreenDimensions(screenDims) {
        this.screenDims = screenDims;
        this.dynamicSizesManager = new CanvasDynamicSizesManager(screenDims);
    }

    _findImageToDraw(imgName) {
        return this.imgsArray.filter(img => img.name === imgName)[0];
    }

    _beginLoadingImage(imageObj, setAllImgsLoaded) {
        imageObj.img.onload = (image) => this._loadImgsAndStartIfReady(image, setAllImgsLoaded);
        imageObj.img.src = imageObj.src;
    }

    _loadImgsAndStartIfReady(image, setAllImgsLoaded) {
        this.imgsToLoadCount--;
        if (this.imgsToLoadCount === 0) {
            setAllImgsLoaded(true);
            this.imgsToLoadCount = this.imgsArray.length;
            this._imgLoadingDoneStart();
        }
    }

    _imgLoadingDoneStart() {
        this.drawAll(false, false);
    }

    _drawBackground() {
        this.canvasContext.beginPath();
        this.canvasContext.fillStyle = "gray";
        this.canvasContext.fillRect(0, 0, 1000, 1000); // TODO: MAKE THESE CONSTANTS
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
        this.canvasContext.drawImage(positionImgObj.img, Math.round(positionCoords.x), Math.round(positionCoords.y));
    }


    _drawPositions() {
        for (let i = 0; i < this.game.allPlayersDealerFirst.length; i++) {
            this._drawPosition(i);
        }
    }

    _drawCard(x, y, angle, card, shouldScale, shouldEnlarge) {
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
        this.canvasContext.translate(Math.round(x), Math.round(y));
        this.canvasContext.rotate(angle * Math.PI / 180);

        this.canvasContext.drawImage(cardImgObj.img,
            0,
            0,
            shouldEnlarge ? 150 : cardSize.width, // TODO: MAKE THE ENLARGED SIZES CONSTANT
            shouldEnlarge ? 200 : cardSize.height
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

    _drawTokens() {
        const tokens = TokenUtils.moneyToTokens(this.thisPlayer.money);
        const tokensColumnMax = Math.max(...Object.values(tokens));
        for (let tokenIdx = 0; tokenIdx < tokensColumnMax; tokenIdx++) {
            const tokensCoords = this.dynamicSizesManager.TOKEN_COORDS(tokenIdx);
            for (let tc = 0; tc < tokensCoords.length; tc++) {
                if (tokens[Object.keys(tokens)[tc]] > tokenIdx) {
                    this._drawToken(tokensCoords[tc].x, tokensCoords[tc].y, tc, false);
                }
            }
        }
    }

    drawAll(drawCards, drawTokens) {
        this.canvasContext.save();
        this.canvasContext.scale(
            this.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR,
            this.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR
        );
        this._drawBackground();
        this._drawTable();
        if (!drawCards) {
            this.canvasContext.restore();
            return;
        }
        this._drawPositions();
        this._drawCards();
        if (drawTokens) {
            this._drawTokens();
        }
        this._drawAllPlayerBetTokens();
        this.canvasContext.restore();
    }

    loadImagesAndStart(screenDims, setAllImgsLoaded) {
        this.screenDims = screenDims;
        for (const image of this.imgsArray) {
            this._beginLoadingImage(image, setAllImgsLoaded);
        }
    }

    _drawToken(x, y, currentTokenColumnIdx, shouldScale, shouldShrink, shrinkedSize) {
        const imgName = TokenUtils.getCurrentTokenColumnImgName(currentTokenColumnIdx);
        const tokenImgObj = this._findImageToDraw(imgName);
        const tokenSize = CanvasDynamicSizesManager.originalSizes.TOKEN;
        if (shouldScale) {
            this.canvasContext.save();
            this.canvasContext.scale(
                this.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR,
                this.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR
            );
        }

        this.canvasContext.drawImage(tokenImgObj.img,
            Math.round(x),
            Math.round(y),
            shouldShrink ? shrinkedSize : tokenSize.width,
            shouldShrink ? shrinkedSize : tokenSize.height
        );

        shouldScale && this.canvasContext.restore();
    }


    enlargeCards(playerRank) { // TODO: EXPORT THIS TO A DIFFERENT DECIDER FILE
        switch (playerRank) {
            case HoverOverType.PLAYER_CARDS[0]: // DEALER
                this._enlargePlayerCards(0);
                break;
            case HoverOverType.PLAYER_CARDS[1]: // 
                this._enlargePlayerCards(1);
                break;
            case HoverOverType.PLAYER_CARDS[2]: // 
                this._enlargePlayerCards(2);
                break;
            case HoverOverType.PLAYER_CARDS[3]: // 
                this._enlargePlayerCards(3);
                break;
            case HoverOverType.PLAYER_CARDS[4]: // 
                this._enlargePlayerCards(4);
                break;
            case HoverOverType.PLAYER_CARDS[5]: // 
                this._enlargePlayerCards(5);
                break;
            case HoverOverType.PLAYER_CARDS[6]: // 
                this._enlargePlayerCards(6);
                break;

            default:
                break;
        }
    }

    _enlargePlayerCards(playerIdx) {
        const player = this.game.allPlayersDealerFirst[playerIdx];
        for (let cardIdx = 0; cardIdx < player.displayedCards.length; cardIdx++) {
            const cardCoords = this.dynamicSizesManager.CARD_COORDS(playerIdx, cardIdx);
            const cardAngle = CanvasDynamicSizesManager.constants.CARD_NUM_OFFSETS[cardIdx].angle;
            const card = player.displayedCards[cardIdx];
            if (card.visibility === "HIDDEN") { // TODO: transform this to enum
                console.log(cardCoords.x, cardCoords.y, 'CanvasManager.js', 'line: ', '206');
                this._drawCard(cardCoords.x, cardCoords.y, cardAngle, undefined, true, true);
            } else {
                this._drawCard(cardCoords.x, cardCoords.y, cardAngle, card, true, true);
            }
        }
    }

    _drawAllPlayerBetTokens() {
        for (let playerIdx = 0; playerIdx < this.game.allPlayersDealerFirst.length; playerIdx++) {
            const player = this.game.allPlayersDealerFirst[playerIdx];
            const tokens = TokenUtils.moneyToTokens(player.bet);
            const tokensColumnMax = Math.max(...Object.values(tokens));
            for (let tokenIdx = 0; tokenIdx < tokensColumnMax; tokenIdx++) {
                const tokensCoords = this.dynamicSizesManager.BET_TOKEN_COORS(playerIdx, tokenIdx);
                for (let tc = 0; tc < tokensCoords.length; tc++) {
                    if (tokens[Object.keys(tokens)[tc]] > tokenIdx) {
                        this._drawToken(tokensCoords[tc].x, tokensCoords[tc].y, tc, false, true, 20);
                    }
                }
            }
        }
    }

    drawCanvasStateToBackupCanvas() {
        this.backupCanvas = document.createElement('canvas');
        this.backupCanvas.width = CanvasDynamicSizesManager.sizeUtils.canvasStyle(this.screenDims).width;
        this.backupCanvas.height = this.screenDims.height
        const destCtx = this.backupCanvas.getContext('2d');
        destCtx.drawImage(this.canvas, 0, 0);
        this.isBackupCanvasDrawn = true;
    }

    drawBackupCanvasStateToCanvas(shouldClearBackupCanvas) {
        const destCtx = this.canvasContext;
        destCtx.drawImage(this.backupCanvas, 0, 0);
        if (shouldClearBackupCanvas) {
            this.backupCanvas.getContext('2d').clearRect(0, 0, this.backupCanvas.width, this.backupCanvas.height);
            this.isBackupCanvasDrawn = false;
        }        
    }


}


export default CanvasManager;