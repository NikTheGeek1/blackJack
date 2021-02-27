package com.blackjack.server.models.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    Player player1;
    Player player2;
    Player dealer;
    Deck deck;
    Game game;

    @BeforeEach
    void setUp() {
        player1 = new Player();
        player2 = new Player();
        List<Player> players = Arrays.asList(player1, player2);
        dealer = new Player();
        deck = new Deck();
        game = new Game(players, dealer, deck);
    }

    @Test
    void canShuffle() {
        List<Card> unshuffled = new ArrayList<>(deck.getCards());
        deck.shuffle();
        assertNotEquals(unshuffled, deck.getCards());
    }

    @Test
    void canResetDeck() {
        deck.shuffle();
        List<Card> shuffled = new ArrayList<>(deck.getCards());
        assertEquals(shuffled, deck.getCards());
        deck.resetDeck();
        assertNotEquals(shuffled, deck.getCards());
    }

    @Test
    void canDealCard() {
        player1.addCard(deck.dealCard());
        assertEquals(1, player1.getCards().size());
    }

    @Test
    void playerWinsWhenPlayerBJDealerSticks() {
        HashMap<String, Player> expectedResults = new HashMap<>();
        player1.setStatus(PlayerStatus.BLACKJACK);
        dealer.setStatus(PlayerStatus.STICK);
        expectedResults.put("Winner", player1);
        expectedResults.put("Loser", dealer);
        assertEquals(expectedResults, GameRound.whoWon(dealer, player1));
    }

    @Test
    void playerWinsWhenPlayerSticksDealerBusted() {
        HashMap<String, Player> expectedResults = new HashMap<>();
        player1.setStatus(PlayerStatus.STICK);
        dealer.setStatus(PlayerStatus.BUSTED);
        expectedResults.put("Winner", player1);
        expectedResults.put("Loser", dealer);
        assertEquals(expectedResults, GameRound.whoWon(dealer, player1));
    }

    @Test
    void dealerWinsIfDealerBJPlayerSticks() {
        HashMap<String, Player> expectedResults = new HashMap<>();
        player1.setStatus(PlayerStatus.STICK);
        dealer.setStatus(PlayerStatus.BLACKJACK);
        expectedResults.put("Winner", dealer);
        expectedResults.put("Loser", player1);
        assertEquals(expectedResults, GameRound.whoWon(dealer, player1));
    }

    @Test
    void dealerWinsIfDealerSticksPlayerBusted() {
        HashMap<String, Player> expectedResults = new HashMap<>();
        player1.setStatus(PlayerStatus.BUSTED);
        dealer.setStatus(PlayerStatus.STICK);
        expectedResults.put("Winner", dealer);
        expectedResults.put("Loser", player1);
        assertEquals(expectedResults, GameRound.whoWon(dealer, player1));
    }

    @Test
    void dealerWinsIfBothBusted() {
        HashMap<String, Player> expectedResults = new HashMap<>();
        player1.setStatus(PlayerStatus.BUSTED);
        dealer.setStatus(PlayerStatus.BUSTED);
        expectedResults.put("Winner", dealer);
        expectedResults.put("Loser", player1);
        assertEquals(expectedResults, GameRound.whoWon(dealer, player1));
    }

    @Test
    void dealerWinsIfBothStickAndTie() {
        HashMap<String, Player> expectedResults = new HashMap<>();
        player1.addCard(new Card(Suit.CLUBS, Rank.NINE));
        player1.setStatus(PlayerStatus.STICK);
        dealer.addCard(new Card(Suit.CLUBS, Rank.NINE));
        dealer.setStatus(PlayerStatus.STICK);
        expectedResults.put("Winner", dealer);
        expectedResults.put("Loser", player1);
        assertEquals(expectedResults, GameRound.whoWon(dealer, player1));
    }


    @Test
    void dealerWinsIfBothSticksAndDealerHasHigherHand() {
        HashMap<String, Player> expectedResults = new HashMap<>();
        player1.addCard(new Card(Suit.CLUBS, Rank.EIGHT));
        player1.setStatus(PlayerStatus.STICK);
        dealer.addCard(new Card(Suit.CLUBS, Rank.NINE));
        dealer.setStatus(PlayerStatus.STICK);
        expectedResults.put("Winner", dealer);
        expectedResults.put("Loser", player1);
        assertEquals(expectedResults, GameRound.whoWon(dealer, player1));
    }

    @Test
    void dealerWinsIfBothBlackJackButDealerHas2CardsPlayerHas3() {
        HashMap<String, Player> expectedResults = new HashMap<>();
        player1.addCard(new Card(Suit.CLUBS, Rank.EIGHT));
        player1.addCard(new Card(Suit.CLUBS, Rank.EIGHT));
        player1.addCard(new Card(Suit.CLUBS, Rank.FIVE));
        player1.setStatus(PlayerStatus.BLACKJACK);
        dealer.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        dealer.addCard(new Card(Suit.CLUBS, Rank.JACK));
        dealer.setStatus(PlayerStatus.BLACKJACK);
        expectedResults.put("Winner", dealer);
        expectedResults.put("Loser", player1);
        assertEquals(expectedResults, GameRound.whoWon(dealer, player1));
    }

    @Test
    void dealerWinsIfBothBlackJackSameNumberOfCards() {
        HashMap<String, Player> expectedResults = new HashMap<>();
        player1.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        player1.addCard(new Card(Suit.CLUBS, Rank.JACK));
        player1.setStatus(PlayerStatus.BLACKJACK);
        dealer.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        dealer.addCard(new Card(Suit.CLUBS, Rank.JACK));
        dealer.setStatus(PlayerStatus.BLACKJACK);
        expectedResults.put("Winner", dealer);
        expectedResults.put("Loser", player1);
        assertEquals(expectedResults, GameRound.whoWon(dealer, player1));
    }

    @Test
    void playerWinsIfBothBJButPlayer2CardsDealer3() {
        HashMap<String, Player> expectedResults = new HashMap<>();
        player1.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        player1.addCard(new Card(Suit.CLUBS, Rank.JACK));
        player1.setStatus(PlayerStatus.BLACKJACK);
        dealer.addCard(new Card(Suit.CLUBS, Rank.ACE1));
        dealer.addCard(new Card(Suit.CLUBS, Rank.JACK));
        dealer.addCard(new Card(Suit.CLUBS, Rank.JACK));
        dealer.setStatus(PlayerStatus.BLACKJACK);
        expectedResults.put("Winner", player1);
        expectedResults.put("Loser", dealer);
        assertEquals(expectedResults, GameRound.whoWon(dealer, player1));
    }



}