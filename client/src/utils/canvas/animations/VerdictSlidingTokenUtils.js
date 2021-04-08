import PlayerStatus from "../../../constants/PlayerStatus";

class VerdictSlidingTokenUtils {

    static instance;
    static setInstance(canvasManager) {
        if (!VerdictSlidingTokenUtils.instance && VerdictSlidingTokenUtils._haveAllPlayersBet(canvasManager)) {
            VerdictSlidingTokenUtils.instance = new VerdictSlidingTokenUtils(canvasManager);
            console.log("SlidingTokenUtils instance created", 'VerdictSlidingTokenUtils.js', 'line: ', '8');
        }
    }

    static _haveAllPlayersBet(canvasManager) {
        const players = canvasManager.game.players;
        const boolArray = players.map(player => player.status !== PlayerStatus.BETTING);
        return boolArray.every(_ => _);
    }

    static getInstance() {
        if (!VerdictSlidingTokenUtils.instance) {
            throw new Error("VerdictSlidingTokenUtils instance has not been initialised");
        }
        return VerdictSlidingTokenUtils.instance;
    }

    static destroy() {
        VerdictSlidingTokenUtils.instance = null;
    }

    static setVerdicts(players) {
        if (!VerdictSlidingTokenUtils.instance) {
            throw new Error("VerdictSlidingTokenUtils instance has not been initialised");
        }
        VerdictSlidingTokenUtils.instance.verdicts = players.map(player => player.status);
    }

    static getData() {
        if (!VerdictSlidingTokenUtils.instance) {
            throw new Error("VerdictSlidingTokenUtils instance has not been initialised");
        }
        return VerdictSlidingTokenUtils.instance._initialValues();
    }

    constructor(canvasManager) {
        this.canvasManager = canvasManager;
        this.playersBetTokens = canvasManager.game.players.map(player => ({ ...player.betTokens }));
        this.verdicts = null;
    }

    _initialValues() {
        const allTokenCoords = [];
        const tokenCurrentCoords = [];
        for (let playerIdx = 0; playerIdx < this.playersBetTokens.length; playerIdx++) {
            const playersArray = [];
            const playersCCArray = [];
            const playerTokens = this.playersBetTokens[playerIdx];
            const tokensColumnMax = Math.max(...Object.values(playerTokens));
            for (let tokenIdx = 0; tokenIdx < tokensColumnMax; tokenIdx++) {
                const tokensArray = [];
                const tokensCCArray = [];
                for (let tc = 0; tc < Object.keys(playerTokens).length; tc++) {
                    if (playerTokens[Object.keys(playerTokens)[tc]] > tokenIdx) {
                        const coords = this.canvasManager.dynamicSizesManager.getCoordsForVerdictTokens(tc, tokenIdx, playerIdx, this.verdicts[playerIdx]);
                        tokensArray.push(coords);
                        tokensCCArray.push({ ...coords.initialCoords });
                    } else {
                        tokensArray.push({});
                        tokensCCArray.push({});
                    }
                }
                playersArray.push(tokensArray);
                playersCCArray.push(tokensCCArray);
            }
            allTokenCoords.push(playersArray);
            tokenCurrentCoords.push(playersCCArray);
        }
        return { tokenCurrentCoords, allTokenCoords, };
    }


}


export default VerdictSlidingTokenUtils;