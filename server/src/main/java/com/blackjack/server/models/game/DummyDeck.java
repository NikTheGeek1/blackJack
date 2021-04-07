package com.blackjack.server.models.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DummyDeck {
    static List<Card> getDummyCards() {
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(Suit.CLUBS, Rank.ACE11));
        cards.add(new Card(Suit.CLUBS, Rank.ACE11));
        cards.add(new Card(Suit.CLUBS, Rank.ACE11));

        cards.add(new Card(Suit.CLUBS, Rank.TEN));
        cards.add(new Card(Suit.CLUBS, Rank.TEN));
        cards.add(new Card(Suit.CLUBS, Rank.TEN));

    return cards;
    }
}
