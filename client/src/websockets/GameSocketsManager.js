import { initSocket } from './web-sockets-game-rep';
import URLs from '../services/DEV-URLs';
import Bet from '../models/matches/Bet';
import PlayerUtils from '../utils/game-utils/players-utils';
import PlayerStatus from '../constants/PlayerStatus';
import PlayerChoiceType from '../models/matches/PlayerChoiceType';

class GameSocketsManager {
    constructor(matchSetter, partiallyMatchSetter, playerSetter, playerChoiceSetter, matchName, thisPlayerEmail) {
        this.socket = null;
        this.updateGameSubscription = null;
        this.matchSetter = matchSetter;
        this.partiallyMatchSetter = partiallyMatchSetter;
        this.playerSetter = playerSetter;
        this.playerChoiceSetter = playerChoiceSetter;
        this.matchName = matchName;
        this.thisPlayerEmail = thisPlayerEmail;
    }


    connect() {
        if (!this.socket) {
            this.socket = initSocket();
        }
        this.socket.connect({}, frame => {
            this._updateGameSubscription();
            this._sendEnterGame();
        });
    }

    _sendEnterGame() {
        this.socket.send(URLs.ENTER_GAME(this.matchName), {}, "entered game");
    }

    _updateGameSubscription() {
        this.updateGameSubscription = this.socket.subscribe(URLs.UPDATE_GAME(this.matchName), (msg) => {
            const bodyParsed = JSON.parse(msg.body);
            const matchParsed = bodyParsed.match;
            const playerChoiceParsed = bodyParsed.playerChoice;
            const thisPlayerUpdated = PlayerUtils.findPlayerByEmail(this.thisPlayerEmail, matchParsed.game.allPlayersDealerFirst);
            if (thisPlayerUpdated.status === PlayerStatus.BETTING &&
                playerChoiceParsed.playerChoiceType === PlayerChoiceType.BET) {
                    // when someone else betted, do not update thisPlayer
                    this.partiallyMatchSetter({match: matchParsed, thisPlayerEmail: this.thisPlayerEmail });
                } else {
                    // update player normally
                    this.matchSetter(matchParsed);
                    this.playerSetter(thisPlayerUpdated);
                }
                this.playerChoiceSetter(playerChoiceParsed); // this setter actually triggers the update so we need it in
            console.log(matchParsed, 'GameSocketsManager.js', 'line: ', '41');
            console.log("updated match", 'GameSocketsManager.js', 'line: ', '41');
        });
    }

    sendStartGame = () => {
        this.socket.send(URLs.START_GAME(this.matchName, this.thisPlayerEmail), {}, "starting game");
    };

    sendBet = (betTokens) => {
        // TODO: validate bet
        if (!!!betTokens) return;
        const bet = new Bet(betTokens, this.thisPlayerEmail);
        this.socket.send(URLs.PLACE_BET(this.matchName), {}, JSON.stringify(bet));
    };

    sendStick = () => {
        this.socket.send(URLs.STICK(this.matchName, this.thisPlayerEmail), {}, "sticking");
    }

    sendDraw = () => {
        this.socket.send(URLs.DRAW(this.matchName, this.thisPlayerEmail), {}, "drawing");
    }

    cleanUp() {
        this.updateGameSubscription?.unsubscribe();
        this.socket.send(URLs.LEAVE_GAME(this.matchName), {}, this.thisPlayerEmail);
        this.socket.disconnect();
    }
}

export default GameSocketsManager;