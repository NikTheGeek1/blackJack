import TokenUtils from './TokenUtils';
import Player from '../../models/matches/Player';

class PlaceTokensAnimationUtils {
    constructor(finalFrame) {
        this.finalFrame = finalFrame;
        const currentFrame = new Player(finalFrame);
        currentFrame.money = 0;
        this.currentFrame = currentFrame;
        this.animationFinished = false;
        this.nextFrameTokenIdx = 0;
        this.shouldDrawToken = [];
        this.tokensFinal = TokenUtils.moneyToTokens(this.finalFrame.money);
        this.tokensCurrent = TokenUtils.moneyToTokens(this.currentFrame.money);
    }


    nextFrame() {
        const allTokensAdded = this._addToken();
        if (allTokensAdded) this.animationFinished = true;
    }


    _addToken() {
        this.shouldDrawToken = [false, false, false, false];
        for (let k = 0; k < Object.keys(this.tokensFinal).length ; k++) {
            const tokenCategory = Object.keys(this.tokensFinal)[k];
            if (this.tokensCurrent[tokenCategory] < this.tokensFinal[tokenCategory]) {
                this.tokensCurrent[tokenCategory] += 1;
                this.nextFrameTokenIdx = this.tokensCurrent[tokenCategory];
                this.currentFrame.money += +tokenCategory;
                this.shouldDrawToken[k] = true;
            }
        }
        return Object.keys(this.tokensCurrent).every(tokenCategory => this.tokensCurrent[tokenCategory] === this.tokensFinal[tokenCategory]);
    }

}

export default PlaceTokensAnimationUtils;