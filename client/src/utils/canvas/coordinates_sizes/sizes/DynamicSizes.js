import SizeUtils from './SizeUtils';

class CanvasDynamicSizes  {
    constructor(screenDims) {
        this.screenDims = screenDims;
        this.CANVAS_SIZE = { width: SizeUtils.canvasStyle(screenDims).width, height: screenDims.height };
        this.TABLE_SIZE = {  width: this.CANVAS_SIZE.width,  height: this.CANVAS_SIZE.width * .6 };
        this.POSISITION_SIZE = { width: this.CANVAS_SIZE.width / 12, height: this.CANVAS_SIZE.width / 12}
    }



}

export default CanvasDynamicSizes;