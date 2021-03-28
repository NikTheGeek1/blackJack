import DynamicCoords from '../coordinates_sizes/coords/DynamicCoords';
import ImgSizes from '../../../constants/canvas/ImgsOriginalSizes';
import Constants from '../../../constants/canvas/Constants';
import HoverOverTypes from './HoverOverTypes';


class CardLocator {
    constructor(mousePos, screenDims, game) {
        this.mousePos = mousePos;
        this.screenDims = screenDims;
        this.game = game;
        this.dynamicCoords = new DynamicCoords(this.screenDims);
    }

    mouseOnCards() {
        const numberOfPayers = this.game.allPlayersDealerFirst.length;
        const onCardsArray = [...Array(7).keys()].map(_ => false);
        for (let playerIdx = 0; playerIdx < numberOfPayers; playerIdx++) {
            const cardsLength = this.game.allPlayersDealerFirst[playerIdx].displayedCards.length;
            const { y1, y2 } = this._calculateY(playerIdx);
            const { x1, x2 } = this._calculateX(playerIdx, cardsLength);
            if (this.mousePos.x > x1 && this.mousePos.x < x2 &&
                this.mousePos.y > y1 && this.mousePos.y < y2) {
                onCardsArray[playerIdx] = true;
                break;
            }
        }
        return HoverOverTypes.PLAYER_CARDS.filter((_, i) => onCardsArray[i])[0];
    }

    _calculateY(playerIdx) {
        const cardBaselineY = this.dynamicCoords.CARD_COORDS(playerIdx, 0).y;
        const y1 = cardBaselineY;
        const y2 = y1 + ImgSizes.CARD.height;
        return { y1, y2 };
    }

    _calculateX(playerIdx, cardsLength) {
        const cardBaselineX = this.dynamicCoords.CARD_COORDS(playerIdx, 0).x;
        const x1 = cardBaselineX;
        const cardNumMultiplier = cardsLength * (Constants.CARD_NUM_OFFSETS[1].x / 2);
        const x2 = cardBaselineX + cardNumMultiplier + ImgSizes.CARD.width;
        return { x1, x2 };
    }
}


export default CardLocator;