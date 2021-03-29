import DynamicCoords from '../coordinates_sizes/coords/DynamicCoords';
import ImgSizes from '../../../constants/canvas/ImgsOriginalSizes';
import Constants from '../../../constants/canvas/Constants';
import SizeUtils from '../coordinates_sizes/sizes/SizeUtils';
import HoverOverTypes from './HoverOverTypes';

class BetTokensLocator {

    constructor(mousePos, screenDims, thisPlayer, allPlayers) {
        this.mousePos = mousePos;
        this.screenDims = screenDims;
        this.thisPlayer = thisPlayer;
        this.dynamicCoords = new DynamicCoords(this.screenDims);
        this.thisPlayerIdx = null;
        this._setPlayerIdx(allPlayers);
    }

    _setPlayerIdx(allPlayers) {
        this.thisPlayerIdx = allPlayers.findIndex(player => player.email === this.thisPlayer.email);
    }

    mouseOnBetTokens() {
        const TOKEN_COLUMNS = ["1", "10", "50", "100", "200", "500"];
        const onTokenArray = TOKEN_COLUMNS.map(_ => false);
        for (let tcIdx = 0; tcIdx < TOKEN_COLUMNS.length; tcIdx++) {
            const tokenColumnName = TOKEN_COLUMNS[tcIdx];
            if (!this.thisPlayer.betTokens[tokenColumnName]) continue;
            const { y1, y2 } = this._calculateY(this.thisPlayer.betTokens[tokenColumnName]);
            const { x1, x2 } = this._calculateX(tcIdx);
            if (this.mousePos.x > x1 && this.mousePos.x < x2 &&
                this.mousePos.y > y1 && this.mousePos.y < y2) {
                onTokenArray[tcIdx] = true;
                break;
            }
        }
        return HoverOverTypes.BET_TOKEN_COLUMNS.filter((_, i) => onTokenArray[i])[0];
    }

    _calculateY(tokensInColumn) {
        const tokenBaselineY = this.dynamicCoords.BET_TOKEN_COORDS(this.thisPlayerIdx, 0)[0].y;
        const y1 = tokenBaselineY + tokensInColumn * Constants.BET_TOKEN_NUM_Y_OFFSET;

        const y2 = tokenBaselineY + (ImgSizes.TOKEN.height - 34);
        return { y1, y2 };
    }

    _calculateX(columnIdx) {
        const tokenBaselineX = this.dynamicCoords.BET_TOKEN_COORDS(this.thisPlayerIdx, 0)[columnIdx].x;
        const imgObj = {width: ImgSizes.TOKEN.width- 34, height: ImgSizes.TOKEN.height- 34};
        const x1 = tokenBaselineX - (ImgSizes.TOKEN.width - 34) / 2 + SizeUtils.halfObject(imgObj).width;
        const x2 = tokenBaselineX + (ImgSizes.TOKEN.width - 34) / 2 + SizeUtils.halfObject(imgObj).width;
        return { x1, x2 };
    }
}

export default BetTokensLocator;
