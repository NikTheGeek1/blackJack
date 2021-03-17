import CanvasImgNames from '../../constants/CanvasImgNames';

class CanvasManager {
    constructor(canvas, screenDims, imgsArray) {
        this.screenDims = screenDims;
        this.canvas = canvas;
        this.canvasContext = canvas.getContext('2d');
        this.imgsArray = imgsArray.map(img => ({ img: new Image(), src: img.src, name: img.name, width: img.width, height: img.height }));
        this.imgsToLoadCount = imgsArray.length;
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
            this._imgLoadingDoneStart();
        }
    }

    _imgLoadingDoneStart() {
        this.drawAll();
    }

    _drawTable() {
        const table = this._findImageToDraw(CanvasImgNames.TABLE).img;
        this.canvasContext.drawImage(table, 0, 0);
    }

    _drawPosition() {
        const position = this._findImageToDraw(CanvasImgNames.POSITION);
        this.canvasContext.drawImage(position.img, 500 - (position.width / 2), 300 - (position.height / 2));
    }
    
    drawAll() {
        this._drawTable();
        this._drawPosition();
    }

    loadImagesAndStart() {
        for (const image of this.imgsArray) {
            this._beginLoadingImage(image);
        }
    }
}


export default CanvasManager;