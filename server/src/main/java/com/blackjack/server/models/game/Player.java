package com.blackjack.server.models.game;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private List<Card> cards;
    private double bet;
    private double money;
    private boolean isDealer;
    private PlayerStatus status;

    public Player() {
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

    public boolean isDealer() {
        return isDealer;
    }

    public void setDealer(boolean dealer) {
        isDealer = dealer;
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

    public void setBet(double bet) {
        this.bet = bet;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public double getBet() {
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
        bet = 0;
        status = PlayerStatus.WAITING;
    }

    public void addCard(Card card) {
        cards.add(card);
    }
}
