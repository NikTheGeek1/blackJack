import User from "../users/User";
import TokenUtils from '../../utils/canvas/TokenUtils';

class Player extends User {
    constructor(fecthedPlayer) {
        super(fecthedPlayer);
        this.displayedCards = fecthedPlayer.displayedCards;
        this.bet = fecthedPlayer.bet;
        this.betTokens = TokenUtils.moneyToTokens(this.bet);
        this.money = fecthedPlayer.money;
        this.tokens = TokenUtils.moneyToTokens(this.money);
        this.revealedCards = fecthedPlayer.revealedCards;
        this.isDealer = fecthedPlayer.isDealer;
        this.status = fecthedPlayer.status;
    }
}

export default Player;