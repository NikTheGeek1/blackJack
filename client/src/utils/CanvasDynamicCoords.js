import SizeUtils from './CanvasSizeUtils';
import CanvasConstants from '../constants/CanvasConstants';
import CanvasDynamicSizes from './CanvasDynamicSizes';
import CanvasSizeUtils from './CanvasSizeUtils';
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
    }

    CARD_COORDS(playerIdx, cardIdx) {

        return {
            x: this.POSISITIONS_COORDS[playerIdx].x + CanvasConstants.Ps_CARD_OFFSETS[playerIdx].x + CanvasConstants.CARD_NUM_OFFSETS[cardIdx].x,
            y: this.POSISITIONS_COORDS[playerIdx].y + CanvasConstants.Ps_CARD_OFFSETS[playerIdx].y + CanvasConstants.CARD_NUM_OFFSETS[cardIdx].y
        };
    }


    centerOfTable_centered(objectSize) {
        return { x: this.TABLE_CENTER_COORDS.x - SizeUtils.halfObject(objectSize).width, y: this.TABLE_CENTER_COORDS.y - SizeUtils.halfObject(objectSize).height };
    }








}

export default CanvasDynamicCoords;