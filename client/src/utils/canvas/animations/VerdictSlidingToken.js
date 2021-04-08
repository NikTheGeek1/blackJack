import PlayerStatus from "../../../constants/PlayerStatus";
import CanvasDynamicSizesManager from "../coordinates_sizes/DynamicManager";
import VerdictSlidingTokenUtils from "./VerdictSlidingTokenUtils";

class VerdictSlidingToken {
    constructor(canvasManager, onFinishCb) {
        this.canvasManager = canvasManager;
        this.backupCanvas = null;
        this.data = null; // { tokenCurrentCoords, allTokenCoords, }
        this.playersBetTokens = null;
        this.onFinishCb = onFinishCb;
    }

    start() {
        VerdictSlidingTokenUtils.setVerdicts(this.canvasManager.game.players);
        this.data = VerdictSlidingTokenUtils.getData();
        this.playersBetTokens = VerdictSlidingTokenUtils.instance.playersBetTokens;
        this.drawCanvasStateToBackupCanvas();
        this._drawSlidingTokenRecursively();
    }

    _drawSlidingTokenRecursively() {
        this.drawBackupCanvasStateToCanvas(false);
        this._drawAllPlayerBetTokensAndIncrement(this.data.tokenCurrentCoords, this.data.allTokenCoords);
        if (this._shouldStopDrawingTokens(this.data.tokenCurrentCoords, this.data.allTokenCoords)) {
            this.drawBackupCanvasStateToCanvas(false);
            this.onFinishCb();
            VerdictSlidingTokenUtils.destroy();
        } else {
            requestAnimationFrame(this._drawSlidingTokenRecursively.bind(this));
        }
    }

    _shouldStopDrawingTokens() {
        const boolArray = [];
        this.data.allTokenCoords.forEach(player => {
            player.forEach(token => {
                token.forEach(tc => {
                    if (Object.keys(tc).length) {
                        boolArray.push(tc.reachedFinalDestination);
                    }
                });
            });
        });
        return boolArray.every(_ => _);
    }

    _drawAllPlayerBetTokensAndIncrement(allPlayerTokensCoords, increments) {
        for (let playerIdx = 0; playerIdx < allPlayerTokensCoords.length; playerIdx++) {
            const playerTokens = this.playersBetTokens[playerIdx];
            const tokensColumnMax = Math.max(...Object.values(playerTokens));
            for (let tokenIdx = 0; tokenIdx < tokensColumnMax; tokenIdx++) {
                for (let tc = 0; tc < 6; tc++) {
                    if (
                        playerTokens[Object.keys(playerTokens)[tc]] > tokenIdx &&
                        !increments[playerIdx][tokenIdx][tc].reachedFinalDestination
                    ) {
                        this.canvasManager._drawToken(
                            allPlayerTokensCoords[playerIdx][tokenIdx][tc].x,
                            allPlayerTokensCoords[playerIdx][tokenIdx][tc].y, tc, true, true, 20
                        );
                        this._increamentCoords(
                            allPlayerTokensCoords[playerIdx][tokenIdx][tc],
                            increments[playerIdx][tokenIdx][tc]
                        );
                    }
                }
            }
        }
    }


    _increamentCoords(currentCoord, increment) {
        if (increment.verdict === PlayerStatus.WON) {
            currentCoord.x -= increment.x;
            currentCoord.y -= increment.y;
        } else {
            currentCoord.x += increment.x;
            currentCoord.y += increment.y;
        }
        this._hasTokenReachedItsFinalDestination(currentCoord, increment);
    }

    _hasTokenReachedItsFinalDestination(currentCoord, allCoords) {
        if (allCoords.verdict === PlayerStatus.WON) {
            allCoords.reachedFinalDestination = currentCoord.y > allCoords.finalY;
        } else {
            allCoords.reachedFinalDestination = currentCoord.y < allCoords.finalY;
        }
    }


    drawCanvasStateToBackupCanvas() {
        this.backupCanvas = document.createElement('canvas');
        this.backupCanvas.width = CanvasDynamicSizesManager.sizeUtils.canvasStyle(this.canvasManager.screenDims).width;
        this.backupCanvas.height = this.canvasManager.screenDims.height
        const destCtx = this.backupCanvas.getContext('2d');
        destCtx.drawImage(this.canvasManager.canvas, 0, 0);
        this.isBackupCanvasDrawn = true;
    }

    drawBackupCanvasStateToCanvas(shouldClearBackupCanvas) {
        const destCtx = this.canvasManager.canvasContext;
        destCtx.drawImage(this.backupCanvas, 0, 0);
        if (shouldClearBackupCanvas) {
            this.backupCanvas.getContext('2d').clearRect(0, 0, this.backupCanvas.width, this.backupCanvas.height);
            this.isBackupCanvasDrawn = false;
        }
    }
}


export default VerdictSlidingToken;