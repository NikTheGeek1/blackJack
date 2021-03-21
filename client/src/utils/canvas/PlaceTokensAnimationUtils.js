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
        this.nextFrameTokenColumnIdx = 0;
        
        this.tokensFinal = TokenUtils.moneyToTokens(this.finalFrame.money);
        this.tokensCurrent = TokenUtils.moneyToTokens(this.currentFrame.money);
    }


    nextFrame() {
        const allTokensAdded = this._addToken();
        if (allTokensAdded) this.animationFinished = true;
    }


    _addToken() {
        for (let k = 0; k < Object.keys(this.tokensFinal).length ; k++) {
            const tokenCategory = Object.keys(this.tokensFinal)[k];
            if (this.tokensCurrent[tokenCategory] < this.tokensFinal[tokenCategory]) {
                this.tokensCurrent[tokenCategory] += 1;
                this.nextFrameTokenIdx = this.tokensCurrent[tokenCategory];
                this.nextFrameTokenColumnIdx = k;
                this.currentFrame.money += +tokenCategory;
                break;
            }
        }
        return Object.keys(this.tokensCurrent).every(tokenCategory => this.tokensCurrent[tokenCategory] === this.tokensFinal[tokenCategory]);
    }

}

export default PlaceTokensAnimationUtils;