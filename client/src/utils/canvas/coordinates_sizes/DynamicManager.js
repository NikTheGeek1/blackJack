import CanvasDynamicCoords from '../coordinates_sizes/coords/DynamicCoords';
import CanvasSizeUtils from './sizes/SizeUtils';
import CanvasImgOriginalSizes from '../../../constants/canvas/ImgsOriginalSizes';
import CanvasConstants from '../../../constants/canvas/Constants';

class CanvasDynamicManager extends CanvasDynamicCoords {

    constructor(screenDims) {
        super(screenDims);   
    }

    static sizeUtils = CanvasSizeUtils;
    static originalSizes = CanvasImgOriginalSizes;
    static constants = CanvasConstants;
}


export default CanvasDynamicManager;