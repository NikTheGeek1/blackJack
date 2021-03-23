class CanvasSizeUtils {
    
    static WIDTH_RESC_FACTOR = .76;

    static halfObject(objectSize) {
        return { width: objectSize.width / 2, height: objectSize.height / 2 };
    }


    static canvasStyle(screenDims) {
        const widthFactor = this.WIDTH_RESC_FACTOR;
        return { width: screenDims.width * widthFactor, height: screenDims.height };
    }
}


export default CanvasSizeUtils;