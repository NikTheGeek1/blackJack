package com.blackjack.server.models.game;

import com.blackjack.server.models.User;

import java.util.ArrayList;
import java.util.List;

public class Player extends User{
    private List<Card> cards;
    private Bet bet;
    private double money;
    private boolean isDealer;
    private PlayerStatus status;

    public Player(User user) {
        super(user);
        status = PlayerStatus.WAITING;
        isDealer = false;
        cards = new ArrayList<>();
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public boolean getIsDealer() {
        return isDealer;
    }

    public void setIsDealer(boolean isDealer) {
        this.isDealer = isDealer;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setBet(Bet bet) throws Exception {
        if (money - bet.getBetValue() < 0) throw new Exception("Not enough money");
        this.bet = bet;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public Bet getBet() {
        return bet;
    }

    public int getCardTotal() {
        int total = 0;
        for (Card card : cards) {
            total += card.getRank().getValue();
        }
        return total;
    }

    public void increaseMoney(double amount) {
        money += amount;
    }

    public void decreaseMoney(double amount) {
        money -= amount;
    }

    public void prepareForNextRound() {
        bet = new Bet(this.getEmail(), 0);
        status = PlayerStatus.WAITING;
    }

    public void addCard(Card card) {
        cards.add(card);
        handleAces();
    }

    public Card getHiddenCard() {
        for (Card card : cards) {
            if (card.getVisibility() == CardVisibility.HIDDEN) return card;
        }
        return null;
    }

    public Card getFirstRevealedCard() {
        for (Card card : cards) {
            if (card.getVisibility() == CardVisibility.REVEALED) return card;
        }
        return null;
    }

    private void handleAces() {
        for (Card card : cards) {
            if (card.getRank() == Rank.ACE11 && getCardTotal() > 21) card.setRank(Rank.ACE1);
        }
    }
}
