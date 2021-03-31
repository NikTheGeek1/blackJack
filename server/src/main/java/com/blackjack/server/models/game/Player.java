package com.blackjack.server.models.game;

import com.blackjack.server.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Player extends User  {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Card> cards;
    private List<Card> displayedCards;
    private double bet;
    private boolean isDealer;
    private PlayerStatus status;

    public Player(User user) {
        super(user);
        status = PlayerStatus.WAITING_GAME;
        isDealer = false;
        cards = new ArrayList<>();
        displayedCards = new ArrayList<>();
    }

    public Player (Player player) {
        super(player);
        this.status = player.getStatus();
        this.isDealer = player.getIsDealer();
        this.cards = player.getCards();
        this.displayedCards = player.displayedCards;
    }

    public Player() {
        super();
        status = PlayerStatus.WAITING_GAME;
        isDealer = false;
        cards = new ArrayList<>();
        displayedCards = new ArrayList<>();
    }

    public void resetCards() {
        this.cards.clear();
        this.displayedCards.clear();
    }

    public List<Card> getDisplayedCards() {
        return displayedCards;
    }

    public void setDisplayedCards() {
        List<Card> dispCards = new ArrayList<>();
        for (Card card : cards) {
            if (card.getVisibility() == CardVisibility.REVEALED) {
                dispCards.add(card);
            }
            else {
                Card hiddenCardCopy = new Card(card.getSuit(), card.getRank());
                dispCards.add(hiddenCardCopy);
            }
        }
        this.displayedCards = dispCards;
    }

    public List<Card> getRevealedCards() {
        List<Card> revCards = this.displayedCards.stream()
                .filter(card -> card.getVisibility() == CardVisibility.REVEALED)
                .collect(Collectors.toList());
        return revCards;
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
            this.setDisplayedCards();
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
        setDisplayedCards();
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
