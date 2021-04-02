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

        this.TOKEN_COLUMNS_COORDS = CanvasConstants.TOKEN_COLUMNS_OFFSETS.map(tco => {
            return {
                x: this.centerOfTable_centered(CanvasImgOriginalSizes.TOKEN).x + tco.x,
                y: this.centerOfTable_centered(CanvasImgOriginalSizes.TOKEN).y + tco.y
            };
        });
    }

    NAMES_COORDS(playerIdx, playerName) {
        const constantCoords = CanvasConstants.NAMES_COORDS[playerIdx];
        return constantCoords;
    };

    

    CARD_COORDS(playerIdx, cardIdx) {
        return {
            x: this.POSISITIONS_COORDS[playerIdx].x + CanvasConstants.Ps_CARD_OFFSETS[playerIdx].x + CanvasConstants.CARD_NUM_OFFSETS[cardIdx].x,
            y: this.POSISITIONS_COORDS[playerIdx].y + CanvasConstants.Ps_CARD_OFFSETS[playerIdx].y + CanvasConstants.CARD_NUM_OFFSETS[cardIdx].y
        };
    }

    TOKEN_COORDS(tokenIdx) {
        return this.TOKEN_COLUMNS_COORDS.map(cc => {
            return {
                x: cc.x + CanvasConstants.TOKENS_NUM_OFFSETS[tokenIdx].x,
                y: cc.y + CanvasConstants.TOKENS_NUM_OFFSETS[tokenIdx].y
            };
        });
    }

    BET_TOKEN_COORDS(playerIdx, tokenIdx) {
        const baselineY = this.CARD_COORDS(playerIdx, 0).y - 30; // TODO: MAKE THESE CONSTANTS
        const baselineX = this.CARD_COORDS(playerIdx, 0).x - 10; // TODO: MAKE THESE CONSTANTS
        return CanvasConstants.BET_COLUMNS_OFFSETS.map(cc => {
            return {
                x: baselineX + CanvasConstants.BET_TOKENS_NUM_OFFSETS[tokenIdx].x + cc.x,
                y: baselineY + CanvasConstants.BET_TOKENS_NUM_OFFSETS[tokenIdx].y + cc.y
            };
        });
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

        const x = (finalFrameCoords.x - initialFrameCoords.x) * .07; // TODO: make this constant
        const y = slope * x;
        return { x: x, y: y, finalY: finalFrameCoords.y, angle: cardAngle };
    }

    getCoordsForPlacingToken(tokenIdx) {
        const finalFrameCoords = this.TOKEN_COORDS(tokenIdx);
        const initialFrameCoords = CanvasConstants.TOKENS_ANIMATION_INITIAL_COORDS;
        
        const slopes = initialFrameCoords.map((_, i) => {
            return this._calculateSlope(initialFrameCoords[i].y, finalFrameCoords[i].y, initialFrameCoords[i].x, finalFrameCoords[i].x);
        });
        
        const X = initialFrameCoords.map((_, i) => {
            return (finalFrameCoords[i].x - initialFrameCoords[i].x) * .07; // TODO: Make this constant
        });
        
        const Y = slopes.map((_, i) => slopes[i] * X[i]);
        
        return slopes.map((_, i) => ({ x: X[i], y: Y[i], finalY: finalFrameCoords[i].y }));
    }

    getCoordsForPlacingBetToken(tokenColumnIdx, tokenIdx, playerIdx) {
        const finalFrameCoords = this.BET_TOKEN_COORDS(playerIdx, tokenIdx)[tokenColumnIdx];
        const initialFrameCoords = this.TOKEN_COORDS(tokenIdx)[tokenColumnIdx];
        
        const slope = this._calculateSlope(initialFrameCoords.y, finalFrameCoords.y, initialFrameCoords.x, finalFrameCoords.x);
        
        const x = (finalFrameCoords.x - initialFrameCoords.x) * .03; // TODO: Make this constant
        
        const y = slope * x;
        
        return {x, y, finalY: finalFrameCoords.y};
    }

    getCoordsForCancellingBetToken(tokenColumnIdx, tokenIdx, playerIdx) {
        const initialFrameCoords = this.BET_TOKEN_COORDS(playerIdx, tokenIdx)[tokenColumnIdx];
        const finalFrameCoords = this.TOKEN_COORDS(tokenIdx)[tokenColumnIdx];
        
        const slope = this._calculateSlope(initialFrameCoords.y, finalFrameCoords.y, initialFrameCoords.x, finalFrameCoords.x);
        
        const x = (initialFrameCoords.x - finalFrameCoords.x) * .03; // TODO: Make this constant
        
        const y = slope * x;
        
        return {x, y, finalY: finalFrameCoords.y};
    }

}

export default CanvasDynamicCoords;