import User from "../users/User";

class Player extends User{
    constructor(name, email, money, revealedCards, bet, isDealer, status, id) {
        super(name, email, money, id);
        this.bet = bet;
        this.money = money;
        this.revealedCards = revealedCards;
        this.isDealer = isDealer;
        this.status = status;
    }
}

export default Player;