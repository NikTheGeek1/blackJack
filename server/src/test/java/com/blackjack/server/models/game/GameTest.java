package com.blackjack.server.models.game;

import com.blackjack.server.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    User user;
    Player player1;
    Player player2;
    Dealer dealer;
    Deck deck;
    GameRound game;

    @BeforeEach
    void setUp() {
        user = new User();
        player1 = new Player(user);
        player2 = new Player(user);
        LinkedList<Player> players = new LinkedList<>();
        players.add(player1);
        players.add(player2);
        dealer = new Dealer(user);
        dealer.setMoney(100);
        dealer.setIsDealer(true);
        deck = new Deck();
        game = new GameRound(players, dealer, deck);
    }

    @Test
    void canDealCards() {
        game.dealCards();
        assertEquals(2, player1.getCards().size());
        assertEquals(2, player2.getCards().size());
        assertEquals(2, dealer.getCards().size());
    }

    @Test
    void oneOfTheTwoDealtCardsIsRevealdTheOtherHidden() {
        game.dealCards();
        assertEquals(CardVisibility.HIDDEN, player1.getCards().get(0).getVisibility());
        assertEquals(CardVisibility.REVEALED, player1.getCards().get(1).getVisibility());
        assertEquals(CardVisibility.HIDDEN, player2.getCards().get(0).getVisibility());
        assertEquals(CardVisibility.REVEALED, player2.getCards().get(1).getVisibility());
        assertEquals(CardVisibility.HIDDEN, dealer.getCards().get(0).getVisibility());
        assertEquals(CardVisibility.REVEALED, dealer.getCards().get(1).getVisibility());
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
        player1.addCard(deck.dealCard(CardVisibility.HIDDEN));
        assertEquals(1, player1.getCards().size());
    }

    @Test
    void playerWinsWhenPlayerBJDealerSticks() {
        HashMap<String, Player> expectedResults = new HashMap<>();
        player1.setStatus(PlayerStatus.BLACKJACK);
        dealer.setStatus(PlayerStatus.STICK);
        expectedResults.put("Winner", player1);
        expectedResults.put("Loser", dealer);
        assertEquals(expectedResults, game.whoWon(dealer, player1));
    }

    @Test
    void playerWinsWhenPlayerSticksDealerBusted() {
        HashMap<String, Player> expectedResults = new HashMap<>();
        player1.setStatus(PlayerStatus.STICK);
        dealer.setStatus(PlayerStatus.BUSTED);
        expectedResults.put("Winner", player1);
        expectedResults.put("Loser", dealer);
        assertEquals(expectedResults, game.whoWon(dealer, player1));
    }

    @Test
    void dealerWinsIfDealerBJPlayerSticks() {
        HashMap<String, Player> expectedResults = new HashMap<>();
        player1.setStatus(PlayerStatus.STICK);
        dealer.setStatus(PlayerStatus.BLACKJACK);
        expectedResults.put("Winner", dealer);
        expectedResults.put("Loser", player1);
        assertEquals(expectedResults, game.whoWon(dealer, player1));
    }

    @Test
    void dealerWinsIfDealerSticksPlayerBusted() {
        HashMap<String, Player> expectedResults = new HashMap<>();
        player1.setStatus(PlayerStatus.BUSTED);
        dealer.setStatus(PlayerStatus.STICK);
        expectedResults.put("Winner", dealer);
        expectedResults.put("Loser", player1);
        assertEquals(expectedResults, game.whoWon(dealer, player1));
    }

    @Test
    void dealerWinsIfBothBusted() {
        HashMap<String, Player> expectedResults = new HashMap<>();
        player1.setStatus(PlayerStatus.BUSTED);
        dealer.setStatus(PlayerStatus.BUSTED);
        expectedResults.put("Winner", dealer);
        expectedResults.put("Loser", player1);
        assertEquals(expectedResults, game.whoWon(dealer, player1));
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
        assertEquals(expectedResults, game.whoWon(dealer, player1));
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
        assertEquals(expectedResults, game.whoWon(dealer, player1));
    }

    @Test
    void playerWinsIfBothSticksAndPlayerHasHigherHand() {
        HashMap<String, Player> expectedResults = new HashMap<>();
        player1.addCard(new Card(Suit.CLUBS, Rank.EIGHT));
        player1.setStatus(PlayerStatus.STICK);
        dealer.addCard(new Card(Suit.CLUBS, Rank.SEVEN));
        dealer.setStatus(PlayerStatus.STICK);
        expectedResults.put("Winner", player1);
        expectedResults.put("Loser", dealer);
        assertEquals(expectedResults, game.whoWon(dealer, player1));
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
        assertEquals(expectedResults, game.whoWon(dealer, player1));
    }

    @Test
    void dealerWinsIfBothBlackJackSameNumberOfCards2() {
        HashMap<String, Player> expectedResults = new HashMap<>();
        player1.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        player1.addCard(new Card(Suit.CLUBS, Rank.JACK));
        player1.setStatus(PlayerStatus.BLACKJACK);
        dealer.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        dealer.addCard(new Card(Suit.CLUBS, Rank.JACK));
        dealer.setStatus(PlayerStatus.BLACKJACK);
        expectedResults.put("Winner", dealer);
        expectedResults.put("Loser", player1);
        assertEquals(expectedResults, game.whoWon(dealer, player1));
    }

    @Test
    void dealerWinsIfBothBlackJackSameNumberOfCards3() {
        HashMap<String, Player> expectedResults = new HashMap<>();
        player1.addCard(new Card(Suit.CLUBS, Rank.ACE1));
        player1.addCard(new Card(Suit.CLUBS, Rank.JACK));
        player1.addCard(new Card(Suit.CLUBS, Rank.JACK));
        player1.setStatus(PlayerStatus.BLACKJACK);
        dealer.addCard(new Card(Suit.CLUBS, Rank.ACE1));
        dealer.addCard(new Card(Suit.CLUBS, Rank.JACK));
        dealer.addCard(new Card(Suit.CLUBS, Rank.JACK));
        dealer.setStatus(PlayerStatus.BLACKJACK);
        expectedResults.put("Winner", dealer);
        expectedResults.put("Loser", player1);
        assertEquals(expectedResults, game.whoWon(dealer, player1));
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
        assertEquals(expectedResults, game.whoWon(dealer, player1));
    }

    @Test
    void playerCanBetIfEnoughMoney() throws Exception {
        player1.setMoney(10);
        player1.setBet(10);
        assertEquals(10, player1.getBet());
    }

    @Test
    void playerCannotBetIfNotEnoughMoney() {
        player1.setMoney(0);
        Exception exception = assertThrows(Exception.class, () -> player1.setBet(10));
    }

    @Test
    void playersMoneyIncreaseWhenTheyWin() {
        player1.setMoney(10);
        player2.setMoney(10);
        try {
            player2.setBet(10);
            player1.setBet(10);
        } catch (Exception e) {
            assertTrue(false);
        }
        player1.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        player1.addCard(new Card(Suit.CLUBS, Rank.JACK));
        player1.setStatus(PlayerStatus.BLACKJACK);
        player2.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        player2.addCard(new Card(Suit.CLUBS, Rank.JACK));
        player2.setStatus(PlayerStatus.BLACKJACK);
        dealer.addCard(new Card(Suit.CLUBS, Rank.JACK));
        dealer.addCard(new Card(Suit.CLUBS, Rank.JACK));
        dealer.setStatus(PlayerStatus.STICK);
        game.verdict();
        assertEquals(20, player1.getMoney());
        assertEquals(20, player2.getMoney());
    }

    @Test
    void playersMoneyDecreaseWhenTheyLose() {
        player1.setMoney(10);
        player2.setMoney(10);
        try {
            player2.setBet(10);
            player1.setBet(10);
        } catch (Exception e) {
            assertTrue(false);
        }
        player1.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        player1.addCard(new Card(Suit.CLUBS, Rank.JACK));
        player1.setStatus(PlayerStatus.BLACKJACK);
        player2.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        player2.addCard(new Card(Suit.CLUBS, Rank.JACK));
        player2.setStatus(PlayerStatus.BLACKJACK);
        dealer.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        dealer.addCard(new Card(Suit.CLUBS, Rank.JACK));
        dealer.setStatus(PlayerStatus.BLACKJACK);
        game.verdict();
        assertEquals(0, player1.getMoney());
        assertEquals(0, player2.getMoney());
    }


    @Test
    void dealersMoneyIncreaseWhenTheyWin() {
        player1.setMoney(10);
        player2.setMoney(10);
        try {
            player2.setBet(10);
            player1.setBet(10);
        } catch (Exception e) {
            assertTrue(false);
        }
        player1.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        player1.addCard(new Card(Suit.CLUBS, Rank.JACK));
        player1.setStatus(PlayerStatus.BLACKJACK);
        player2.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        player2.addCard(new Card(Suit.CLUBS, Rank.JACK));
        player2.setStatus(PlayerStatus.BLACKJACK);
        dealer.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        dealer.addCard(new Card(Suit.CLUBS, Rank.JACK));
        dealer.setStatus(PlayerStatus.BLACKJACK);
        game.verdict();
        assertEquals(120, dealer.getMoney());
    }

    @Test
    void dealersMoneyDecreaseWhenTheyLose() {
        player1.setMoney(10);
        player2.setMoney(10);
        try {
            player2.setBet(10);
            player1.setBet(10);
        } catch (Exception e) {
            assertTrue(false);
        }
        player1.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        player1.addCard(new Card(Suit.CLUBS, Rank.JACK));
        player1.setStatus(PlayerStatus.BLACKJACK);
        player2.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        player2.addCard(new Card(Suit.CLUBS, Rank.JACK));
        player2.setStatus(PlayerStatus.BLACKJACK);
        dealer.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        dealer.addCard(new Card(Suit.CLUBS, Rank.EIGHT));
        dealer.addCard(new Card(Suit.CLUBS, Rank.JACK));
        dealer.setStatus(PlayerStatus.BUSTED);
        game.verdict();
        assertEquals(80, dealer.getMoney());
    }

    public void turnSimulator(Player player) {
        player.hiddenCard().setVisibility(CardVisibility.REVEALED);
        while (player.cardTotal() < 16) {
            player.addCard(deck.dealCard(CardVisibility.REVEALED));
        }
        if (player.cardTotal() == 21) player.setStatus(PlayerStatus.BLACKJACK);
        if (player.cardTotal() > 21) player.setStatus(PlayerStatus.BUSTED);
        if (player.cardTotal() < 21) player.setStatus(PlayerStatus.STICK);
    }

    @Test
    void ace11ShouldStayAce11IfOnlyOneAce() {
        player1.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        Rank cardRank = player1.getCards().get(0).getRank();
        assertEquals(Rank.ACE11, cardRank);
    }

    @Test
    void oneAce11ShouldBecomeAce1IfTwoAces() {
        player1.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        player1.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        Rank cardRank1 = player1.getCards().get(0).getRank();
        Rank cardRank2 = player1.getCards().get(1).getRank();
        assertNotEquals(cardRank1, cardRank2);
    }

    @Test
    void oneAce11ShouldBecomeAce1IfThreeAces() {
        player1.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        player1.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        player1.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        Rank cardRank1 = player1.getCards().get(0).getRank();
        Rank cardRank2 = player1.getCards().get(1).getRank();
        Rank cardRank3 = player1.getCards().get(2).getRank();
        assertEquals(cardRank1, cardRank2);
        assertNotEquals(cardRank2, cardRank3);
    }

    @Test
    void ace11ShouldBecomeAce1IfCardTotalMoreThan21() {
        player1.addCard(new Card(Suit.CLUBS, Rank.ACE11));
        player1.addCard(new Card(Suit.CLUBS, Rank.EIGHT));
        player1.addCard(new Card(Suit.CLUBS, Rank.NINE));
        assertEquals(18, player1.cardTotal());
    }

    @Test
    void simulateAGameFromTheBeginning() throws Exception{
        deck.shuffle();
        game.dealCards();
        for (Player player : game.getPlayers()) {
            player.setMoney(10);
            player.setBet(10);
            turnSimulator(player);
        }
        turnSimulator(dealer);
        game.verdict();
        assertNotEquals(10, player1.getMoney());
        assertNotEquals(10, player2.getMoney());
        assertEquals(100 + (10 - player1.getMoney()) + (10 - player2.getMoney()), dealer.getMoney());
    }
}