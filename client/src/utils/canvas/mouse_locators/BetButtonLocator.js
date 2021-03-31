import Constants from '../../../constants/canvas/Constants';
import HoverOverTypes from './HoverOverTypes';

class BetButtonLocator {

    constructor(mousePos) {
        this.mousePos = mousePos;
    }

    
    mouseOnBetButton() {
        const { y1, y2 } = this._calculateY();
        const { x1, x2 } = this._calculateX();
        if (this.mousePos.x > x1 && this.mousePos.x < x2 &&
            this.mousePos.y > y1 && this.mousePos.y < y2) {
            return HoverOverTypes.BET_BUTTON;
        }
    }

    _calculateY() {
        const y1 = Constants.BET_BUTTON_COORDS.y - Constants.BET_BUTTON_SIZE.height;
        const y2 = Constants.BET_BUTTON_COORDS.y;
        return { y1, y2 };
    }

    _calculateX() {
        const x1 = Constants.BET_BUTTON_COORDS.x;
        const x2 = x1 + Constants.BET_BUTTON_SIZE.width;
        return { x1, x2 };
    }
}


export default BetButtonLocator;
