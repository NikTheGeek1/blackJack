import CanvasDynamicCoords from './CanvasDynamicCoords';
import CanvasSizeUtils from './CanvasSizeUtils';
import CanvasImgOriginalSizes from '../constants/CanvasImgsOriginalSizes';
import CanvasConstants from '../constants/CanvasConstants';

class CanvasDynamicManager extends CanvasDynamicCoords {

    constructor(screenDims) {
        super(screenDims);   
    }

    static sizeUtils = CanvasSizeUtils;
    static originalSizes = CanvasImgOriginalSizes;
    static constants = CanvasConstants;
}


export default CanvasDynamicManager;