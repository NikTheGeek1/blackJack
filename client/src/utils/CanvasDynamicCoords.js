import SizeUtils from './CanvasSizeUtils';
import CanvasConstants from '../constants/CanvasConstants';
import CanvasDynamicSizes from './CanvasDynamicSizes';
import CanvasImgOriginalSizes from '../constants/CanvasImgsOriginalSizes';

class CanvasDynamicCoords extends CanvasDynamicSizes {

    constructor(screenDims) {
        super(screenDims);
        this.screenDims = screenDims;
        this.CANVAS_CENTER_COORDS = {
            x: SizeUtils.halfObject(this.CANVAS_SIZE).width,
            y: SizeUtils.halfObject(this.CANVAS_SIZE).height
        };

        this.TABLE_COORDS = { x: 0, y: 0 };
        this.TABLE_CENTER_COORDS = {
            x: SizeUtils.halfObject(CanvasImgOriginalSizes.TABLE).width,
            y: SizeUtils.halfObject(CanvasImgOriginalSizes.TABLE).height
        };


        this.POSISITIONS_COORDS = [
            {
                x: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).x - CanvasConstants.DEALER_X_OFFSET,
                y: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).y + CanvasConstants.DEALER_Y_OFFSET
            },
            {
                x: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).x - CanvasConstants.Ps_1_6_POSITION_OFFSET_X,
                y: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).y + CanvasConstants.Ps_1_6_POSITION_OFFSET_Y
            },
            {
                x: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).x - CanvasConstants.Ps_2_5_POSITION_OFFSET_X,
                y: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).y + CanvasConstants.Ps_2_5_POSITION_OFFSET_Y
            },
            {
                x: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).x - CanvasConstants.Ps_3_4_POSITION_OFFSET_X,
                y: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).y + CanvasConstants.Ps_3_4_POSITION_OFFSET_Y
            },
            {
                x: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).x + CanvasConstants.Ps_3_4_POSITION_OFFSET_X,
                y: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).y + CanvasConstants.Ps_3_4_POSITION_OFFSET_Y
            },
            {
                x: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).x + CanvasConstants.Ps_2_5_POSITION_OFFSET_X,
                y: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).y + CanvasConstants.Ps_2_5_POSITION_OFFSET_Y
            },
            {
                x: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).x + CanvasConstants.Ps_1_6_POSITION_OFFSET_X,
                y: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).y + CanvasConstants.Ps_1_6_POSITION_OFFSET_Y
            }
        ];

        this.TOKEN_COLUMNS_COORDS = [
            {
                x: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).x + CanvasConstants.TOKEN_COLUMN1_OFFSET_X,
                y: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).y + CanvasConstants.TOKEN_COLUMN1_OFFSET_Y
            },
            {
                x: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).x + CanvasConstants.TOKEN_COLUMN2_OFFSET_X,
                y: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).y + CanvasConstants.TOKEN_COLUMN2_OFFSET_Y
            },
            {
                x: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).x + CanvasConstants.TOKEN_COLUMN3_OFFSET_X,
                y: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).y + CanvasConstants.TOKEN_COLUMN3_OFFSET_Y
            },
            {
                x: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).x + CanvasConstants.TOKEN_COLUMN4_OFFSET_X,
                y: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).y + CanvasConstants.TOKEN_COLUMN4_OFFSET_Y
            }
        ];
    }

    CARD_COORDS(playerIdx, cardIdx) {
        return {
            x: this.POSISITIONS_COORDS[playerIdx].x + CanvasConstants.Ps_CARD_OFFSETS[playerIdx].x + CanvasConstants.CARD_NUM_OFFSETS[cardIdx].x,
            y: this.POSISITIONS_COORDS[playerIdx].y + CanvasConstants.Ps_CARD_OFFSETS[playerIdx].y + CanvasConstants.CARD_NUM_OFFSETS[cardIdx].y
        };
    }

    TOKEN_COORDS(tokenIdx, tokenColumnIdx) {
        return {
            x: this.TOKEN_COLUMNS_COORDS[tokenColumnIdx].x + CanvasConstants.TOKENS_NUM_OFFSETS[tokenIdx].x,
            y: this.TOKEN_COLUMNS_COORDS[tokenColumnIdx].y + CanvasConstants.TOKENS_NUM_OFFSETS[tokenIdx].y
        };
    }


    centerOfTable_centered(objectSize) {
        return { x: this.TABLE_CENTER_COORDS.x - SizeUtils.halfObject(objectSize).width, y: this.TABLE_CENTER_COORDS.y - SizeUtils.halfObject(objectSize).height };
    }

    _calculateSlope(y1, y2, x1, x2) {
        return (y2 - y1) / (x2 - x1);
    }
    getCoordsForDealingCard(playerIdx, cardIdx) {
        const cardAngle = CanvasConstants.CARD_NUM_OFFSETS[cardIdx].angle;
        const finalFrameCoords = this.CARD_COORDS(playerIdx, cardIdx);
        const initialFrameCoords = CanvasConstants.DEALING_CARD_INITIAL_COORDS;
        const slope = this._calculateSlope(initialFrameCoords.y, finalFrameCoords.y, initialFrameCoords.x, finalFrameCoords.x);

        const x = (finalFrameCoords.x - initialFrameCoords.x) * .03; // TODO: make this constant
        const y = slope * x;
        return { x: x, y: y, finalY: finalFrameCoords.y, angle: cardAngle };
    }

    getCoordsForPlacingToken(tokenIdx, tokenColumnIdx) {
        const finalFrameCoords = this.TOKEN_COORDS(tokenIdx, tokenColumnIdx);
        const initialFrameCoords = CanvasConstants.TOKENS_ANIMATION_INITIAL_COORDS;
        const slope = this._calculateSlope(initialFrameCoords.y, finalFrameCoords.y, initialFrameCoords.x, finalFrameCoords.x);
        const x = (finalFrameCoords.x - initialFrameCoords.x) * .03; // TODO: make this constant
        const y = slope * x;
        return { x: x, y: y, finalY: finalFrameCoords.y };
    }

}

export default CanvasDynamicCoords;