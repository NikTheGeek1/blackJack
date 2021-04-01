import User from "../users/User";

class Player extends User {
    constructor(fecthedPlayer) {
        super(fecthedPlayer);
        this.displayedCards = fecthedPlayer.displayedCards;
        this.betTokens = fecthedPlayer.betTokens;
        this.tokens = fecthedPlayer.tokens;
        this.isDealer = fecthedPlayer.isDealer;
        this.status = fecthedPlayer.status;
    }

    getRevealedCards () {
        return this.displayedCards.filter(card => card.visibility === "REVEALED"); // TODO: CHANGE THIS TO ENUM
    }
}

export default Player;