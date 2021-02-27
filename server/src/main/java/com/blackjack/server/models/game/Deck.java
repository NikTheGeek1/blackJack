package com.blackjack.server.models.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        this.setCards();
    }

    public List<Card> getCards() {
        return cards;
    }

    private void setCards() {
        for (Rank rank : Rank.values()) {
            if (rank == Rank.ACE1) continue;
            for (Suit suit : Suit.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public void resetDeck() {
        cards.clear();
        setCards();
    }

    public Card dealCard() {
        return cards.remove(0);
    }
}
