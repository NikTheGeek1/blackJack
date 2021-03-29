import Player from '../../../models/matches/Player';
import TokenUtils from '../TokenUtils';

class PlaceTokensAnimationUtils {
    constructor(finalFrame) {
        this.finalFrame = finalFrame;
        const currentFrame = new Player(finalFrame);
        currentFrame.tokens = TokenUtils.moneyToTokens(0);
        this.currentFrame = currentFrame;
        this.animationFinished = false;
        this.nextFrameTokenIdx = 0;
        this.tokensFinal = this.finalFrame.tokens;
        this.tokensCurrent = this.currentFrame.tokens;
        this.shouldDrawToken = Object.keys(this.tokensCurrent).map(tokenCategory => this.tokensCurrent[tokenCategory] !== this.tokensFinal[tokenCategory]);
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
                this.shouldDrawToken[k] = true;
            }
        }
        return Object.keys(this.tokensCurrent).every(tokenCategory => this.tokensCurrent[tokenCategory] === this.tokensFinal[tokenCategory]);
    }

}

export default PlaceTokensAnimationUtils;