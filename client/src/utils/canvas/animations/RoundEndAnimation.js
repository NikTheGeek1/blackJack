import MessagesManager from "../draw_messages/MessagesManager";

class RoundEndAnimation {
    constructor(canvasManager, onFinish) {
        this.canvasManager = canvasManager;
        this.onFinish = onFinish;
        this.thisPlayerVerdict = this._getThisPlayerVerdict();
    }

    _getThisPlayerVerdict() {
        return this.canvasManager.thisPlayer.status;
    }

    _isThisPlayerDealer() {
        return this.canvasManager.thisPlayer.isDealer;
    }

    start() {
        if (this._isThisPlayerDealer()) {
            this.onFinish();
            MessagesManager.anotherMessageIsDisplayed = false;
        } else {
            MessagesManager.drawVerdict(this.canvasManager, this.thisPlayerVerdict, this.onFinish);
        }
    }


    _persistFrame() {
        this.canvasManager.drawAll(true, true);
    }
}



export default RoundEndAnimation;