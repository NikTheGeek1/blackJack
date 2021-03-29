import DynamicCoords from '../coordinates_sizes/coords/DynamicCoords';
import ImgSizes from '../../../constants/canvas/ImgsOriginalSizes';
import Constants from '../../../constants/canvas/Constants';
import SizeUtils from '../coordinates_sizes/sizes/SizeUtils';
import HoverOverTypes from './HoverOverTypes';

class TokensLocatorUtils {

    constructor(mousePos, screenDims, thisPlayer) {
        this.mousePos = mousePos;
        this.screenDims = screenDims;
        this.thisPlayer = thisPlayer;
        this.dynamicCoords = new DynamicCoords(this.screenDims);
    }

    mouseOnTokens() {
        const TOKEN_COLUMNS = ["1", "10", "50", "100", "200", "500"];
        const onTokenArray = TOKEN_COLUMNS.map(_ => false);
        for (let tcIdx = 0; tcIdx < TOKEN_COLUMNS.length; tcIdx++) {
            const tokenColumnName = TOKEN_COLUMNS[tcIdx];
            if (!this.thisPlayer.tokens[tokenColumnName]) continue;
            const { y1, y2 } = this._calculateY(this.thisPlayer.tokens[tokenColumnName]);
            const { x1, x2 } = this._calculateX(tcIdx);
            if (this.mousePos.x > x1 && this.mousePos.x < x2 &&
                this.mousePos.y > y1 && this.mousePos.y < y2) {
                onTokenArray[tcIdx] = true;
                break;
            }
        }
        return HoverOverTypes.TOKEN_COLUMNS.filter((_, i) => onTokenArray[i])[0];
    }

    _calculateY(tokensInColumn) {
        const tokenBaselineY = this.dynamicCoords.TOKEN_COORDS(0)[0].y;
        const y1 = tokensInColumn > Constants.TOKENS_IN_EACH_WRAP ?
            tokenBaselineY + Constants.TOKENS_IN_EACH_WRAP * Constants.TOKEN_NUM_Y_OFFSET :
            tokenBaselineY + tokensInColumn * Constants.TOKEN_NUM_Y_OFFSET;

        const y2 = tokenBaselineY + Math.floor(tokensInColumn / Constants.TOKENS_IN_EACH_WRAP) * (Constants.TOKEN_WRAP_MULTIPLIER / 2) + ImgSizes.TOKEN.height;
        return { y1, y2 };
    }

    _calculateX(columnIdx) {
        const tokenBaselineX = this.dynamicCoords.TOKEN_COORDS(1)[columnIdx].x;
        const x1 = tokenBaselineX - ImgSizes.TOKEN.width / 2 + SizeUtils.halfObject(ImgSizes.TOKEN).width;
        const x2 = tokenBaselineX + ImgSizes.TOKEN.width / 2 + SizeUtils.halfObject(ImgSizes.TOKEN).width;
        return { x1, x2 };
    }
}

export default TokensLocatorUtils;
