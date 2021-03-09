import User from "../users/User";

class Player extends User{
    constructor(name, email, password, money, cardTotal, firstRevealedCard, hiddenCard, cards, bet, isDealer, status, id) {
        super(name, email, password, money, id);
        this.cards = cards;
        this.bet = bet;
        this.money = money;
        this.cardTotal = cardTotal;
        this.firstRevealedCard = firstRevealedCard;
        this.hiddenCard = hiddenCard;
        this.isDealer = isDealer;
        this.status = status;
    }
}

export default Player;