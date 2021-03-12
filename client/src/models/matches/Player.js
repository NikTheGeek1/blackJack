import User from "../users/User";

class Player extends User {
    constructor(fecthedPlayer) {
        super(fecthedPlayer);
        this.bet = fecthedPlayer.bet;
        this.money = fecthedPlayer.money;
        this.revealedCards = fecthedPlayer.revealedCards;
        this.isDealer = fecthedPlayer.isDealer;
        this.status = fecthedPlayer.status;
    }
}

export default Player;