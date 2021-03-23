import SizeUtils from '../sizes/SizeUtils';
import CanvasConstants from '../../../../constants/canvas/Constants';
import CanvasDynamicSizes from '../sizes/DynamicSizes';
import CanvasImgOriginalSizes from '../../../../constants/canvas/ImgsOriginalSizes';

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
            },
            {
                x: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).x + CanvasConstants.TOKEN_COLUMN5_OFFSET_X,
                y: this.centerOfTable_centered(CanvasImgOriginalSizes.POSITION).y + CanvasConstants.TOKEN_COLUMN5_OFFSET_Y
            }
        ];
    }

    CARD_COORDS(playerIdx, cardIdx) {
        return {
            x: this.POSISITIONS_COORDS[playerIdx].x + CanvasConstants.Ps_CARD_OFFSETS[playerIdx].x + CanvasConstants.CARD_NUM_OFFSETS[cardIdx].x,
            y: this.POSISITIONS_COORDS[playerIdx].y + CanvasConstants.Ps_CARD_OFFSETS[playerIdx].y + CanvasConstants.CARD_NUM_OFFSETS[cardIdx].y
        };
    }

    TOKEN_COORDS(tokenIdx) {
        return [{
            x: this.TOKEN_COLUMNS_COORDS[0].x + CanvasConstants.TOKENS_NUM_OFFSETS[tokenIdx].x,
            y: this.TOKEN_COLUMNS_COORDS[0].y + CanvasConstants.TOKENS_NUM_OFFSETS[tokenIdx].y
        },
        {
            x: this.TOKEN_COLUMNS_COORDS[1].x + CanvasConstants.TOKENS_NUM_OFFSETS[tokenIdx].x,
            y: this.TOKEN_COLUMNS_COORDS[1].y + CanvasConstants.TOKENS_NUM_OFFSETS[tokenIdx].y
        },
        {
            x: this.TOKEN_COLUMNS_COORDS[2].x + CanvasConstants.TOKENS_NUM_OFFSETS[tokenIdx].x,
            y: this.TOKEN_COLUMNS_COORDS[2].y + CanvasConstants.TOKENS_NUM_OFFSETS[tokenIdx].y
        },
        {
            x: this.TOKEN_COLUMNS_COORDS[3].x + CanvasConstants.TOKENS_NUM_OFFSETS[tokenIdx].x,
            y: this.TOKEN_COLUMNS_COORDS[3].y + CanvasConstants.TOKENS_NUM_OFFSETS[tokenIdx].y
        }, 
        {
            x: this.TOKEN_COLUMNS_COORDS[4].x + CanvasConstants.TOKENS_NUM_OFFSETS[tokenIdx].x,
            y: this.TOKEN_COLUMNS_COORDS[4].y + CanvasConstants.TOKENS_NUM_OFFSETS[tokenIdx].y
        }];
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

    getCoordsForPlacingToken(tokenIdx) {
        const finalFrameCoords = this.TOKEN_COORDS(tokenIdx);
        const initialFrameCoords = CanvasConstants.TOKENS_ANIMATION_INITIAL_COORDS;
        const slope1 = this._calculateSlope(initialFrameCoords.y, finalFrameCoords[0].y, initialFrameCoords.x, finalFrameCoords[0].x);
        const slope2 = this._calculateSlope(initialFrameCoords.y, finalFrameCoords[1].y, initialFrameCoords.x, finalFrameCoords[1].x);
        const slope3 = this._calculateSlope(initialFrameCoords.y, finalFrameCoords[2].y, initialFrameCoords.x, finalFrameCoords[2].x);
        const slope4 = this._calculateSlope(initialFrameCoords.y, finalFrameCoords[3].y, initialFrameCoords.x, finalFrameCoords[3].x);
        const slope5 = this._calculateSlope(initialFrameCoords.y, finalFrameCoords[4].y, initialFrameCoords.x, finalFrameCoords[4].x);
        const x1 = (finalFrameCoords[0].x - initialFrameCoords.x) * .07; // TODO: make this constant
        const x2 = (finalFrameCoords[1].x - initialFrameCoords.x) * .07; // TODO: make this constant
        const x3 = (finalFrameCoords[2].x - initialFrameCoords.x) * .07; // TODO: make this constant
        const x4 = (finalFrameCoords[3].x - initialFrameCoords.x) * .07; // TODO: make this constant
        const x5 = (finalFrameCoords[4].x - initialFrameCoords.x) * .07; // TODO: make this constant
        const y1 = slope1 * x1;
        const y2 = slope2 * x2;
        const y3 = slope3 * x3;
        const y4 = slope4 * x4;
        const y5 = slope5 * x5;
        return [
            { x: x1, y: y1, finalY: finalFrameCoords[0].y },
            { x: x2, y: y2, finalY: finalFrameCoords[1].y },
            { x: x3, y: y3, finalY: finalFrameCoords[2].y },
            { x: x4, y: y4, finalY: finalFrameCoords[3].y },
            { x: x5, y: y5, finalY: finalFrameCoords[4].y },
        ];
    }

}

export default CanvasDynamicCoords;