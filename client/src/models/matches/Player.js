import User from "../users/User";

class Player extends User {
    constructor(fetchedPlayer, deepCopyFlag) {
        let copiedPlayer = fetchedPlayer;
        if (deepCopyFlag) {
            const displayedCards = [...fetchedPlayer.displayedCards];
            const tokens = { ...fetchedPlayer.tokens };
            const betTokens = { ...fetchedPlayer.betTokens };
            const isDealer = fetchedPlayer.isDealer;
            const status = fetchedPlayer.status;
            copiedPlayer = { ...fetchedPlayer, displayedCards, betTokens, tokens, isDealer, status };
        }
        super(copiedPlayer);
        this.displayedCards = copiedPlayer.displayedCards;
        this.betTokens = copiedPlayer.betTokens;
        this.tokens = copiedPlayer.tokens;
        this.isDealer = copiedPlayer.isDealer;
        this.status = copiedPlayer.status;
    }


    getRevealedCards() {
        return this.displayedCards.filter(card => card.visibility === "REVEALED"); // TODO: CHANGE THIS TO ENUM
    }
}

export default Player;