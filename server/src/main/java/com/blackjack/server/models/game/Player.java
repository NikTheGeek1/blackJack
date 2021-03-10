package com.blackjack.server.models.game;

import com.blackjack.server.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Player extends User {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Card> cards;
    private List<Card> revealedCards;
    private double bet;
    private boolean isDealer;
    private PlayerStatus status;

    public Player(User user) {
        super(user);
        status = PlayerStatus.WAITING_GAME;
        isDealer = false;
        cards = new ArrayList<>();
        revealedCards = new ArrayList<>();
    }

    public void resetCards() {
        this.cards.clear();
        this.revealedCards.clear();
    }

    public List<Card> getRevealedCards() {
        return revealedCards;
    }

    public void setRevealedCards() {
        List<Card> revCards = new ArrayList<>();
        for (Card card : cards) {
            if (card.getVisibility() == CardVisibility.REVEALED) revCards.add(card);
        }
        this.revealedCards = revCards;
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
        if (status == PlayerStatus.PLAYING) {
            this.hiddenCard().setVisibility(CardVisibility.REVEALED);
            this.setRevealedCards();
            statusAfterPlaying();
        }
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setBet(double bet) throws ArithmeticException {
        if (super.getMoney() - bet < 0) throw new ArithmeticException("Not enough money");
        this.bet = bet;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public double getBet() {
        return bet;
    }

    public int cardTotal() {
        int total = 0;
        for (Card card : cards) {
            total += card.getRank().getValue();
        }
        return total;
    }

    public void prepareForNextRound() {
        bet = 0;
        status = PlayerStatus.BETTING;
    }

    public void addCard(Card card) {
        cards.add(card);
        handleAces();
        setRevealedCards();
    }

    public void statusAfterPlaying () {
        if (this.cardTotal() == 21) this.setStatus(PlayerStatus.BLACKJACK);
        if (this.cardTotal() > 21) this.setStatus(PlayerStatus.BUSTED);
    }

    public void drawCard(Card card) {
        this.addCard(card);
        statusAfterPlaying();
    }

    public Card hiddenCard() {
        for (Card card : cards) {
            if (card.getVisibility() == CardVisibility.HIDDEN) return card;
        }
        return null;
    }

    public Card firstRevealedCard() {
        for (Card card : cards) {
            if (card.getVisibility() == CardVisibility.REVEALED) return card;
        }
        return null;
    }

    private void handleAces() {
        for (Card card : cards) {
            if (card.getRank() == Rank.ACE11 && cardTotal() > 21) card.setRank(Rank.ACE1);
        }
    }

}
