import CanvasImgNames from '../../constants/CanvasImgNames';
import CanvasDynamicSizesManager from '../../utils/CanvasDynamicManager';
import RevealedCardUtils from '../../utils/RevealedCardUtils';
import PlayerChoiceType from '../../models/matches/PlayerChoiceType';
import CanvasDealingCardAnimationUtils from '../../utils/CanvasDealingCardAnimationUtils';
import TokenUtils from '../../utils/canvas/TokenUtils';
import PlaceTokensAnimationUtils from '../../utils/canvas/PlaceTokensAnimationUtils';

class CanvasManager {
    constructor(canvas, screenDims, imgsArray) {
        this.screenDims = screenDims;
        this.dynamicSizesManager = new CanvasDynamicSizesManager(screenDims);
        this.canvas = canvas;
        this.thisPlayer = null; // TODO: test that when a change is made on this object within the CanvasManager class, the change is reflected on the original object (store)
        this.mousePos = { x: 0, y: 0 };
        this.canvasContext = canvas.getContext('2d');
        this.imgsArray = imgsArray.map(img => ({ img: new Image(), src: img.src, name: img.name }));
        this.imgsToLoadCount = imgsArray.length;
        this.onFinishAnimationCb = null;
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

    _drawBackground() {
        this.canvasContext.fillStyle = "gray";
        this.canvasContext.fillRect(0, 0, 1000, 1000);
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

    _drawFourTokens(token1Coords, token2Coords, token3Coords, token4Coords, shouldScale) {
        this._drawToken(token4Coords.x, token4Coords.y, 3, shouldScale);
        this._drawToken(token2Coords.x, token2Coords.y, 1, shouldScale);
        this._drawToken(token1Coords.x, token1Coords.y, 0, shouldScale);
        this._drawToken(token3Coords.x, token3Coords.y, 2, shouldScale);
    }


    _drawTokens() {
        const tokens = TokenUtils.moneyToTokens(this.thisPlayer.money);
        const tokensColumnMax = Math.max(...Object.values(tokens));
        const lastDrawnToken = [];
        for (let tokenIdx = 1; tokenIdx < tokensColumnMax; tokenIdx++) {
            const tokensCoords = this.dynamicSizesManager.TOKEN_COORDS(tokenIdx);
            if (tokens[Object.keys(tokens)[0]] >= tokenIdx) {
                this._drawToken(tokensCoords[0].x, tokensCoords[0].y, 0, false);
                lastDrawnToken[0] = tokenIdx;
            } else {
                const thisTokensCoords = this.dynamicSizesManager.TOKEN_COORDS(lastDrawnToken[0]);
                this._drawToken(thisTokensCoords[0].x, thisTokensCoords[0].y, 0, false)
            }

            if (tokens[Object.keys(tokens)[1]] >= tokenIdx) {
                this._drawToken(tokensCoords[1].x, tokensCoords[1].y, 1, false);
                lastDrawnToken[1] = tokenIdx;
            } else {
                const thisTokensCoords = this.dynamicSizesManager.TOKEN_COORDS(lastDrawnToken[1]);
                this._drawToken(thisTokensCoords[1].x, thisTokensCoords[1].y, 1, false)
            }

            if (tokens[Object.keys(tokens)[2]] >= tokenIdx) {
                this._drawToken(tokensCoords[2].x, tokensCoords[2].y, 2, false);
                lastDrawnToken[2] = tokenIdx;
            } else {
                const thisTokensCoords = this.dynamicSizesManager.TOKEN_COORDS(lastDrawnToken[2]);
                this._drawToken(thisTokensCoords[2].x, thisTokensCoords[2].y, 2, false)
            }

            if (tokens[Object.keys(tokens)[3]] >= tokenIdx) {
                this._drawToken(tokensCoords[3].x, tokensCoords[3].y, 3, false);
                lastDrawnToken[3] = tokenIdx;
            } else {
                const thisTokensCoords = this.dynamicSizesManager.TOKEN_COORDS(lastDrawnToken[3]);
                this._drawToken(thisTokensCoords[3].x, thisTokensCoords[3].y, 3, false)
            }

            if (tokens[Object.keys(tokens)[4]] >= tokenIdx) {
                this._drawToken(tokensCoords[4].x, tokensCoords[4].y, 4, false);
                lastDrawnToken[4] = tokenIdx;
            } else {
                const thisTokensCoords = this.dynamicSizesManager.TOKEN_COORDS(lastDrawnToken[4]);
                this._drawToken(thisTokensCoords[4].x, thisTokensCoords[4].y, 4, false)
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
        this._drawBackground();
        this._drawTable();
        if (!this.game) {
            this.canvasContext.restore();
            return;
        }
        this._drawPositions();
        this._drawCards();
        if (this.thisPlayer) {
            this._drawTokens();
        }
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

    updateDrawing(game, playerChoice, thisPlayer, onFinishCb) {
        switch (playerChoice.playerChoiceType) {
            case PlayerChoiceType.GAME_STARTED_DEALING:
                this.onFinishAnimationCb = onFinishCb;
                this._dealingAnimation(game, () => this._placeTokensAnimation(thisPlayer));
                break;
            default:
                break;
        }
    }

    _dealingCardRecursive(dealingCardsAnimationUtils, onFinishCb) {
        let x = CanvasDynamicSizesManager.constants.DEALING_CARD_INITIAL_COORDS.x;
        let y = CanvasDynamicSizesManager.constants.DEALING_CARD_INITIAL_COORDS.y;
        const playerIdx = dealingCardsAnimationUtils.nextFramePlayerIdx;
        const cardIdx = dealingCardsAnimationUtils.nextFramePlayerCardIdx;
        const allCardCoords = this.dynamicSizesManager.getCoordsForDealingCard(playerIdx, cardIdx);
        const cardAngle = allCardCoords.angle;
        const cardInterval = setInterval(() => {
            if (!dealingCardsAnimationUtils.animationFinished) {
                this._persistFrameGame(dealingCardsAnimationUtils.currentFrame);
                this._drawCard(x, y, cardAngle, null, true);
                y = y + allCardCoords.y;
                x = x + allCardCoords.x;
                if (y > allCardCoords.finalY) {
                    dealingCardsAnimationUtils.nextFrame();
                    clearInterval(cardInterval);
                    this._dealingCardRecursive(dealingCardsAnimationUtils, onFinishCb);
                }
            } else {
                clearInterval(cardInterval);
                this.drawAll();
                onFinishCb();
            }
        }, CanvasDynamicSizesManager.constants.CARD_INTERVAL);
    }

    _drawTokensRecursively(placeTokensUtils) {
        const tokensInitialCoords = CanvasDynamicSizesManager.constants.TOKENS_ANIMATION_INITIAL_COORDS;
        let x1 = tokensInitialCoords.x;
        let y1 = tokensInitialCoords.y - 140;
        let x2 = tokensInitialCoords.x;
        let y2 = tokensInitialCoords.y - 260;
        let x3 = tokensInitialCoords.x;
        let y3 = tokensInitialCoords.y - 380;
        let x4 = tokensInitialCoords.x;
        let y4 = tokensInitialCoords.y - 500;
        let x5 = tokensInitialCoords.x;
        let y5 = tokensInitialCoords.y;

        const tokenIdx = placeTokensUtils.nextFrameTokenIdx;
        const allTokenCoords = this.dynamicSizesManager.getCoordsForPlacingToken(tokenIdx);
        const tokensFinalCoords = this.dynamicSizesManager.TOKEN_COORDS(tokenIdx);

        const tokenInterval = setInterval(() => {
            if (!placeTokensUtils.animationFinished) {
                this.drawAll();

                let shouldDrawToken = [
                    y1 < allTokenCoords[0].finalY && placeTokensUtils.shouldDrawToken[0],
                    y2 < allTokenCoords[1].finalY && placeTokensUtils.shouldDrawToken[1],
                    y3 < allTokenCoords[2].finalY && placeTokensUtils.shouldDrawToken[2],
                    y4 < allTokenCoords[3].finalY && placeTokensUtils.shouldDrawToken[3],
                    y5 < allTokenCoords[4].finalY && placeTokensUtils.shouldDrawToken[4]
                ];
                let thisTokenIdx = 0;
                if (shouldDrawToken[thisTokenIdx]) this._drawToken(x1, y1, thisTokenIdx, true)
                else if (y1 >= allTokenCoords[thisTokenIdx].finalY && placeTokensUtils.shouldDrawToken[thisTokenIdx]) this._drawToken(tokensFinalCoords[thisTokenIdx].x, tokensFinalCoords[thisTokenIdx].y, thisTokenIdx, true);
                thisTokenIdx = 1;
                if (shouldDrawToken[thisTokenIdx]) this._drawToken(x2, y2, thisTokenIdx, true)
                else if (y2 >= allTokenCoords[thisTokenIdx].finalY && placeTokensUtils.shouldDrawToken[thisTokenIdx]) this._drawToken(tokensFinalCoords[thisTokenIdx].x, tokensFinalCoords[thisTokenIdx].y, thisTokenIdx, true);
                thisTokenIdx = 2;
                if (shouldDrawToken[thisTokenIdx]) this._drawToken(x3, y3, thisTokenIdx, true)
                else if (y3 >= allTokenCoords[thisTokenIdx].finalY && placeTokensUtils.shouldDrawToken[thisTokenIdx]) this._drawToken(tokensFinalCoords[thisTokenIdx].x, tokensFinalCoords[thisTokenIdx].y, thisTokenIdx, true);
                thisTokenIdx = 3;
                if (shouldDrawToken[thisTokenIdx]) this._drawToken(x4, y4, thisTokenIdx, true)
                else if (y4 >= allTokenCoords[thisTokenIdx].finalY && placeTokensUtils.shouldDrawToken[thisTokenIdx]) this._drawToken(tokensFinalCoords[thisTokenIdx].x, tokensFinalCoords[thisTokenIdx].y, thisTokenIdx, true);
                thisTokenIdx = 4;
                if (shouldDrawToken[thisTokenIdx]) this._drawToken(x5, y5, thisTokenIdx, true)
                else if (y5 >= allTokenCoords[thisTokenIdx].finalY && placeTokensUtils.shouldDrawToken[thisTokenIdx]) this._drawToken(tokensFinalCoords[thisTokenIdx].x, tokensFinalCoords[thisTokenIdx].y, thisTokenIdx, true);
                
                y1 += allTokenCoords[0].y;
                x1 += allTokenCoords[0].x;
                y2 += allTokenCoords[1].y;
                x2 += allTokenCoords[1].x;
                y3 += allTokenCoords[2].y;
                x3 += allTokenCoords[2].x;
                y4 += allTokenCoords[3].y;
                x4 += allTokenCoords[3].x;
                y5 += allTokenCoords[4].y;
                x5 += allTokenCoords[4].x;
                if (shouldDrawToken.every(_ => !_)) {
                    placeTokensUtils.nextFrame();
                    this._persistFrameTokens(placeTokensUtils.currentFrame);
                    this._drawTokensRecursively(placeTokensUtils);
                    clearInterval(tokenInterval);
                }
            } else {
                clearInterval(tokenInterval);
                this.onFinishAnimationCb();
            }
        }, 30);
    }

    _drawToken(x, y, currentTokenColumnIdx, shouldScale) {
        const imgName = TokenUtils.getCurrentTokenColumnImgName(currentTokenColumnIdx);
        const tokenImgObj = this._findImageToDraw(imgName);
        const tokenSize = CanvasDynamicSizesManager.originalSizes.TOKEN;
        // rotate the canvas to the specified degrees
        if (shouldScale) {
            this.canvasContext.save();
            this.canvasContext.scale(
                this.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR,
                this.screenDims.width / CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR
            );
        }

        this.canvasContext.drawImage(tokenImgObj.img,
            x,
            y,
            tokenSize.width,
            tokenSize.height
        );

        shouldScale && this.canvasContext.restore();

    }

    _placeTokensAnimation(thisPlayer) {
        const placeTokensUtils = new PlaceTokensAnimationUtils(thisPlayer);
        this._drawTokensRecursively(placeTokensUtils);

        // {
        //     this.onFinishAnimationCb(); // when the animation finishes unset the player choice so we can begin playing
        //     this.onFinishAnimationCb = null;
        // }
    }

    _dealingAnimation(game, onFinishCb) {
        const dealingCardsAnimationUtils = new CanvasDealingCardAnimationUtils(game);
        this._dealingCardRecursive(dealingCardsAnimationUtils, onFinishCb);
    }

    _persistFrameGame(game) {
        this.game = game;
        this.drawAll();
    }
    _persistFrameTokens(thisPlayer) {
        this.thisPlayer = thisPlayer;
        this.drawAll();
    }
}


export default CanvasManager;