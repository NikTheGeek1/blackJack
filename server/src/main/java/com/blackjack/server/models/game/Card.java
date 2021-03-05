package com.blackjack.server.models.game;

public class Card {

    private final Suit suit;
    private Rank rank;
    private CardVisibility visibility;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
        this.visibility = CardVisibility.HIDDEN;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public CardVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(CardVisibility visibility) {
        this.visibility = visibility;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }
}
