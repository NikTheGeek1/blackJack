import MessagesManager from "../draw_messages/MessagesManager";
import VerdictSlidingToken from './VerdictSlidingToken';

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
        this.canvasManager.drawAll(true, true);
        const vst = new VerdictSlidingToken(this.canvasManager, () => {});
        if (this._isThisPlayerDealer()) {
            MessagesManager.drawDealersLastStatus(this.canvasManager, this.canvasManager.thisPlayer.status, this.onFinish, () => vst.start());
        } else {
            MessagesManager.drawVerdict(this.canvasManager, this.thisPlayerVerdict, this.onFinish, () => vst.start());
        }
        
    }
}



export default RoundEndAnimation;