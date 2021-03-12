package com.blackjack.server;

import com.blackjack.server.models.User;
import com.blackjack.server.models.game.*;
import com.blackjack.server.models.match.GamePrivacy;
import com.blackjack.server.models.match.GameType;
import com.blackjack.server.models.match.Match;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

enum CustomAssertions {
    FIRST_ROUND_FIRST_PLAYER_GOT_BJ,
    FIRST_ROUND_FIRST_PLAYER_DID_NOT_GOT_BJ,
    PLAYER1_HAD_BJ_FROM_FIRST_HAND,
    PLAYER2_HAD_BJ_FROM_FIRST_HAND,
    DEALER_HAD_BJ_FROM_FIRST_HAND,
    PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND,
    PLAYER2_DID_NOT_HAVE_BJ_FROM_FIRST_HAND,
    DEALER_DID_NOT_HAVE_BJ_FROM_FIRST_HAND,
}


class Helper3 {

    HashMap<CustomAssertions, Boolean> customAssertions = new HashMap<CustomAssertions, Boolean>();
    Match match;

    Dealer dealer;
    Player player1;
    Player player2;


    public Helper3() {
        addCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
        addCustomAssertion(CustomAssertions.PLAYER2_HAD_BJ_FROM_FIRST_HAND);
        addCustomAssertion(CustomAssertions.DEALER_HAD_BJ_FROM_FIRST_HAND);
        addCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        addCustomAssertion(CustomAssertions.PLAYER2_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        addCustomAssertion(CustomAssertions.DEALER_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);

    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public void addCustomAssertion(CustomAssertions assertion) {
        customAssertions.put(assertion, false);
    }

    public void passCustomAssertion(CustomAssertions assertion) {
        customAssertions.put(assertion, true);
    }

    public void removeCustomAssertion(CustomAssertions assertion) {
        customAssertions.remove(assertion);
    }


    public void addDealerToMatch(User user) {
        match.addUser(user);
        GamePrep.setUpGameOrAddPlayer(match);
        assertNotNull(match.getGame());
        assertNotNull(match.getGame().getDealer());
        assertEquals(true, match.getGame().getDealer().getIsDealer());
        assertEquals(PlayerStatus.WAITING_GAME, match.getGame().getDealer().getStatus());
        assertEquals(0, match.getGame().getDealer().getCards().size());
        assertEquals(0, match.getGame().getDealer().getRevealedCards().size());
    }

    public void addUserToMatch(User user, int totalNumOfUsers) {
        match.addUser(user);
        GamePrep.setUpGameOrAddPlayer(match);
        assertNotNull(match.getGame());
        assertEquals(totalNumOfUsers, match.getGame().getAllPlayersDealerFirst().size());
        assertEquals(totalNumOfUsers-1, match.getGame().getPlayers().size());
        assertEquals(PlayerStatus.WAITING_GAME, match.getGame().getPlayerByEmail(user.getEmail()).getStatus());
    }

    public void startGame() {
        GamePrep.startGame(match);
        for (Player player : match.getGame().getPlayers()) {
            assertFalse(player.getIsDealer());
            assertEquals(2, player.getCards().size());
            assertEquals(1, player.getRevealedCards().size());
            assertEquals(0, player.getBet());
            assertEquals(PlayerStatus.BETTING, player.getStatus());
        }
        assertTrue(match.getGame().getDealer().getIsDealer());
        assertEquals(2, match.getGame().getDealer().getCards().size());
        assertEquals(1, match.getGame().getDealer().getRevealedCards().size());
        assertEquals(0, match.getGame().getDealer().getBet());
        assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
        assertFalse(match.getGame().isVerdictOut());
        for (int i = 0; i < match.getGame().getPlayers().size(); i++) {
            switch (i) {
                case 0: {
                    this.player1 = match.getGame().getPlayers().get(0);
                    break;
                }
                case 1: {
                    this.player2 = match.getGame().getPlayers().get(1);
                    break;
                }
            }
        }
        this.dealer = match.getGame().getDealer();
    }

    public void playerBet(Bet bet) {
        // first player after dealer bets. their bet value should
        // be equals to what they bet. they should be in a WAITING_TURN status
        Player player = match.getGame().getPlayerByEmail(bet.getPlayerEmail());
        match.getGame().placeBet(bet.getPlayerEmail(), bet.getBetValue());
        assertEquals(PlayerStatus.WAITING_TURN, player.getStatus());
        assertEquals(bet.getBetValue(), player.getBet());
        assertEquals(1, player.getRevealedCards().size());
    }


    public void playerTurn() {
        Player nextPlayer = match.getGame().grabPlayingPlayer();
        if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {

            if (player1 != null && nextPlayer.getEmail() == player1.getEmail()) {
                passCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
                assertEquals(PlayerStatus.BLACKJACK, player1.getStatus());
            } else if (player2 != null && nextPlayer.getEmail() == player2.getEmail()) {
                passCustomAssertion(CustomAssertions.PLAYER2_HAD_BJ_FROM_FIRST_HAND);
                assertEquals(player2, match.getGame().getPlayerWhoJustGotDealtBlackJack());
                assertEquals(PlayerStatus.BLACKJACK, player2.getStatus());
            } else if (nextPlayer.getEmail() == dealer.getEmail()) {
                passCustomAssertion(CustomAssertions.DEALER_HAD_BJ_FROM_FIRST_HAND);
                assertEquals(dealer, match.getGame().getPlayerWhoJustGotDealtBlackJack());
                assertEquals(PlayerStatus.BLACKJACK, dealer.getStatus());
            }

            changeTurn();
        } else {
            if (player1 != null && nextPlayer.getEmail() == player1.getEmail()) {

                passCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
            } else if (player2 != null && nextPlayer.getEmail() == player2.getEmail()) {

                passCustomAssertion(CustomAssertions.PLAYER2_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
                assertEquals(PlayerStatus.PLAYING, player2.getStatus());
            } else if (nextPlayer.getEmail() == dealer.getEmail()) {

                passCustomAssertion(CustomAssertions.DEALER_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
                assertEquals(PlayerStatus.PLAYING, dealer.getStatus());

            }
            match.getGame().sticks();
        }
    }

    public void changeTurn() {
        match.getGame().nextTurn();
    }

    public void checkIfFinishedProperly () {
        for (Player player : match.getGame().getPlayers()) {
            if (player.getStatus() != PlayerStatus.WON &&
                    player.getStatus() != PlayerStatus.LOST ) {
                if (player.getStatus() == PlayerStatus.WAITING_GAME) continue;
                fail("PLAYER SHOULD EITHER BE LOST OR WON BUT WAS: " + player.getStatus());
            }
        }
        if (dealer.getStatus() != PlayerStatus.STICK &&
                dealer.getStatus() != PlayerStatus.BLACKJACK &&
                dealer.getStatus() == PlayerStatus.WAITING_GAME) {
            fail("PLAYER SHOULD EITHER STICK OR BJ BUT WAS: " + dealer.getStatus());
        }
        assertTrue(match.getGame().isVerdictOut());
    }




    public void playerLeaves(Player player) {
        GamePrep.dropOutManager(match, player.getEmail());
    }

}
class DroppingInAndOutOfGame {

    User user1;
    User user2;
    User user3;
    User user4;
    User user5;
    User user6;
    User user7;
    Bet bet1;
    Bet bet2;
    Bet bet3;
    Bet bet4;
    Bet bet5;
    Bet bet6;
    Bet bet7;
    Match match;


    @BeforeEach
    void setUp() {
        user1 = new User("aa", "aa", "aa");
        user1.setMoney(1000);
        user2 = new User("bb", "bb", "bb");
        user2.setMoney(1000);
        user3 = new User("cc", "cc", "cc");
        user3.setMoney(1000);
        user4 = new User("dd", "dd", "dd");
        user4.setMoney(1000);
        user5 = new User("ee", "ee", "ee");
        user5.setMoney(1000);
        user6 = new User("ff", "ff", "ff");
        user6.setMoney(1000);
        user7 = new User("ww", "ww", "ww");
        user7.setMoney(1000);
        bet1 = new Bet("aa", 1);
        bet2 = new Bet("bb", 1);
        bet3 = new Bet("cc", 1);
        bet4 = new Bet("dd", 1);
        bet5 = new Bet("ee", 1);
        bet6 = new Bet("ff", 1);
        bet7 = new Bet("ww", 1);

        match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
    }

    private void sendChangedRound(Match match) {
        match.getGame().nextRound();
    }
    private void sendChangedTurn(Match match) {
        match.getGame().nextTurn();
    }


    // 2 PLAYERS
    @Test
    void shouldBeAbleToPlayWithTwoUsers() {
        Helper3 helper3 = new Helper3();
        helper3.removeCustomAssertion(CustomAssertions.PLAYER2_HAD_BJ_FROM_FIRST_HAND);
        helper3.removeCustomAssertion(CustomAssertions.PLAYER2_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        for (int i = 0; i < 100000; i++) {
//            System.out.println( i );
            user1 = new User("aa", "aa", "aa");
            user1.setMoney(100000);
            user7 = new User("ww", "ww", "ww");
            user7.setMoney(100000);
            match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
            helper3.setMatch(match);
            helper3.addDealerToMatch(user7);
            helper3.addUserToMatch(user1, 2);

            helper3.startGame();
            Player player1 = match.getGame().getPlayers().get(0);
            Dealer dealer = match.getGame().getDealer();
            helper3.playerBet(bet1);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();



//            // if the playerWhoJustGotDealtBJ is not null, then our player had blackjack
//            // we then change the turn and turn playerWhoJustGotDealtBJ to null again.
//            // at this point the game can have taken lot's of different directions.
//            // the sendChangeTurn will keep running until the next player doesn't have BJ
//            // that means that 3-4 players can all have BJ, and the next turn goes to the 5 player
//            // we amend for that by having and if statement and checking the status of each player
//            // from now on. If they all had black jack, the round should be through and the players
//            // in a betting round
//            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                wentThroughEveryScenario.put("Player1 had BJ from first hand", true);
//                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                sendChangedTurn(match);
//                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            }
//
//            // if the bet has reset, that means the game is over (all had BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("All had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//            if (player1.getStatus() != PlayerStatus.BLACKJACK) { // the player didn't have BJ
//                wentThroughEveryScenario.put("Player1 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player1.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Player3 got BJ from first hand", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == player3);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                }
//            }
//
//            // if the bet has reset, that means the game is over (player3 and dealer BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("Player3 and dealer had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//
//            if (player3.getStatus() != PlayerStatus.BLACKJACK) { // the player3 didn't have BJ
//                wentThroughEveryScenario.put("Player3 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player3.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player3.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Dealer got BJ from first hand,  game was reset", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == dealer);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                    assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                    for (Player player : match.getGame().getPlayers()) {
//                        assertEquals(PlayerStatus.BETTING, player.getStatus());
//                    }
//                    continue;
//                }
//            }
//
//
//
//
//
//            // the next player, dealer in that case, should only be in a playing status, since if they had
//            // bj, the game would been through.
//            // they decide to stick. then, verdict should also be out, since
//            // the last player who his turn finished was the dealer. The dealer doesn't get
//            // neither LOST or WON status from the verdict, since he might won in one turn and lost in another
//            // so, the dealer has the same status as his last choice: STICK in that case.
//            // The player on the other hand, either have a won or a lost status.
//            assertTrue(dealer.getStatus() == PlayerStatus.PLAYING);
//            match.getGame().sticks();
//            assertTrue(match.getGame().isVerdictOut());
//            assertTrue(dealer.getStatus() == PlayerStatus.STICK);
//            assertTrue(player1.getStatus() == PlayerStatus.WON ||
//                    player1.getStatus() == PlayerStatus.LOST);
//            assertTrue(player3.getStatus() == PlayerStatus.WON ||
//                    player3.getStatus() == PlayerStatus.LOST);
//            if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
//                fail("WE SHOULD NOT BE HERE SINCE IF THE DEALER HAD ALSO BJ THE GAME SHOULD BE THROUGH ALREADY" +
//                        "BUT THIS MIGHT BE RELEVANT TO OTHER TESTS SO I LEAVE IT IN");
//                assertEquals(PlayerStatus.LOST, match.getGame().getPlayers().get(0).getStatus());
//            }
//            assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            wentThroughEveryScenario.put("All players stick, verdict is out", true);
//            sendChangedRound(match);
//            assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//            for (Player player : match.getGame().getPlayers()) {
//                assertEquals(PlayerStatus.BETTING, player.getStatus());
//            }
//            continue;
//        }
//

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    // 2 PLAYERS, PLAYER LEAVES
    @Test
    void onePlayerOneDealer_PlayerDropsOutAfterBettingAnotherPlayerGetsInAndPlays() {
        Helper3 helper3 = new Helper3();
        helper3.removeCustomAssertion(CustomAssertions.PLAYER2_HAD_BJ_FROM_FIRST_HAND);
        helper3.removeCustomAssertion(CustomAssertions.PLAYER2_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);

        for (int i = 0; i < 100000; i++) {
//            System.out.println( i );
            user1 = new User("aa", "aa", "aa");
            user1.setMoney(100000);

            user7 = new User("ww", "ww", "ww");
            user7.setMoney(100000);
            match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
            helper3.setMatch(match);
            helper3.addDealerToMatch(user7);
            helper3.addUserToMatch(user1, 2);

            helper3.startGame();
            Player player1 = match.getGame().getPlayers().get(0);
            Dealer dealer = match.getGame().getDealer();
            helper3.playerBet(bet1);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();

            helper3.playerLeaves(player1);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            helper3.addUserToMatch(user2, 2);

            helper3.startGame();
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
            Player player2 = match.getGame().getPlayerByEmail(user2.getEmail());
            helper3.playerBet(bet2);

            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
//            // if the playerWhoJustGotDealtBJ is not null, then our player had blackjack
//            // we then change the turn and turn playerWhoJustGotDealtBJ to null again.
//            // at this point the game can have taken lot's of different directions.
//            // the sendChangeTurn will keep running until the next player doesn't have BJ
//            // that means that 3-4 players can all have BJ, and the next turn goes to the 5 player
//            // we amend for that by having and if statement and checking the status of each player
//            // from now on. If they all had black jack, the round should be through and the players
//            // in a betting round
//            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                wentThroughEveryScenario.put("Player1 had BJ from first hand", true);
//                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                sendChangedTurn(match);
//                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            }
//
//            // if the bet has reset, that means the game is over (all had BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("All had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//            if (player1.getStatus() != PlayerStatus.BLACKJACK) { // the player didn't have BJ
//                wentThroughEveryScenario.put("Player1 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player1.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Player3 got BJ from first hand", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == player3);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                }
//            }
//
//            // if the bet has reset, that means the game is over (player3 and dealer BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("Player3 and dealer had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//
//            if (player3.getStatus() != PlayerStatus.BLACKJACK) { // the player3 didn't have BJ
//                wentThroughEveryScenario.put("Player3 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player3.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player3.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Dealer got BJ from first hand,  game was reset", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == dealer);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                    assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                    for (Player player : match.getGame().getPlayers()) {
//                        assertEquals(PlayerStatus.BETTING, player.getStatus());
//                    }
//                    continue;
//                }
//            }
//
//
//
//
//
//            // the next player, dealer in that case, should only be in a playing status, since if they had
//            // bj, the game would been through.
//            // they decide to stick. then, verdict should also be out, since
//            // the last player who his turn finished was the dealer. The dealer doesn't get
//            // neither LOST or WON status from the verdict, since he might won in one turn and lost in another
//            // so, the dealer has the same status as his last choice: STICK in that case.
//            // The player on the other hand, either have a won or a lost status.
//            assertTrue(dealer.getStatus() == PlayerStatus.PLAYING);
//            match.getGame().sticks();
//            assertTrue(match.getGame().isVerdictOut());
//            assertTrue(dealer.getStatus() == PlayerStatus.STICK);
//            assertTrue(player1.getStatus() == PlayerStatus.WON ||
//                    player1.getStatus() == PlayerStatus.LOST);
//            assertTrue(player3.getStatus() == PlayerStatus.WON ||
//                    player3.getStatus() == PlayerStatus.LOST);
//            if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
//                fail("WE SHOULD NOT BE HERE SINCE IF THE DEALER HAD ALSO BJ THE GAME SHOULD BE THROUGH ALREADY" +
//                        "BUT THIS MIGHT BE RELEVANT TO OTHER TESTS SO I LEAVE IT IN");
//                assertEquals(PlayerStatus.LOST, match.getGame().getPlayers().get(0).getStatus());
//            }
//            assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            wentThroughEveryScenario.put("All players stick, verdict is out", true);
//            sendChangedRound(match);
//            assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//            for (Player player : match.getGame().getPlayers()) {
//                assertEquals(PlayerStatus.BETTING, player.getStatus());
//            }
//            continue;
//        }
//

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }


    @Test
    void onePlayerOneDealer_PlayerDropsOutBeforeBettingAnotherPlayerGetsInAndPlays()  {
        Helper3 helper3 = new Helper3();
        helper3.removeCustomAssertion(CustomAssertions.PLAYER2_HAD_BJ_FROM_FIRST_HAND);
        helper3.removeCustomAssertion(CustomAssertions.PLAYER2_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);

        for (int i = 0; i < 100000; i++) {
//            System.out.println( i );
            user1 = new User("aa", "aa", "aa");
            user1.setMoney(100000);

            user7 = new User("ww", "ww", "ww");
            user7.setMoney(100000);
            match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
            helper3.setMatch(match);
            helper3.addDealerToMatch(user7);
            helper3.addUserToMatch(user1, 2);

            helper3.startGame();
            Player player1 = match.getGame().getPlayers().get(0);
            Dealer dealer = match.getGame().getDealer();

            helper3.playerLeaves(player1);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            helper3.addUserToMatch(user2, 2);

            helper3.startGame();
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
            Player player2 = match.getGame().getPlayerByEmail(user2.getEmail());
            helper3.playerBet(bet2);

            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
//            // if the playerWhoJustGotDealtBJ is not null, then our player had blackjack
//            // we then change the turn and turn playerWhoJustGotDealtBJ to null again.
//            // at this point the game can have taken lot's of different directions.
//            // the sendChangeTurn will keep running until the next player doesn't have BJ
//            // that means that 3-4 players can all have BJ, and the next turn goes to the 5 player
//            // we amend for that by having and if statement and checking the status of each player
//            // from now on. If they all had black jack, the round should be through and the players
//            // in a betting round
//            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                wentThroughEveryScenario.put("Player1 had BJ from first hand", true);
//                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                sendChangedTurn(match);
//                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            }
//
//            // if the bet has reset, that means the game is over (all had BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("All had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//            if (player1.getStatus() != PlayerStatus.BLACKJACK) { // the player didn't have BJ
//                wentThroughEveryScenario.put("Player1 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player1.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Player3 got BJ from first hand", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == player3);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                }
//            }
//
//            // if the bet has reset, that means the game is over (player3 and dealer BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("Player3 and dealer had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//
//            if (player3.getStatus() != PlayerStatus.BLACKJACK) { // the player3 didn't have BJ
//                wentThroughEveryScenario.put("Player3 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player3.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player3.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Dealer got BJ from first hand,  game was reset", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == dealer);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                    assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                    for (Player player : match.getGame().getPlayers()) {
//                        assertEquals(PlayerStatus.BETTING, player.getStatus());
//                    }
//                    continue;
//                }
//            }
//
//
//
//
//
//            // the next player, dealer in that case, should only be in a playing status, since if they had
//            // bj, the game would been through.
//            // they decide to stick. then, verdict should also be out, since
//            // the last player who his turn finished was the dealer. The dealer doesn't get
//            // neither LOST or WON status from the verdict, since he might won in one turn and lost in another
//            // so, the dealer has the same status as his last choice: STICK in that case.
//            // The player on the other hand, either have a won or a lost status.
//            assertTrue(dealer.getStatus() == PlayerStatus.PLAYING);
//            match.getGame().sticks();
//            assertTrue(match.getGame().isVerdictOut());
//            assertTrue(dealer.getStatus() == PlayerStatus.STICK);
//            assertTrue(player1.getStatus() == PlayerStatus.WON ||
//                    player1.getStatus() == PlayerStatus.LOST);
//            assertTrue(player3.getStatus() == PlayerStatus.WON ||
//                    player3.getStatus() == PlayerStatus.LOST);
//            if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
//                fail("WE SHOULD NOT BE HERE SINCE IF THE DEALER HAD ALSO BJ THE GAME SHOULD BE THROUGH ALREADY" +
//                        "BUT THIS MIGHT BE RELEVANT TO OTHER TESTS SO I LEAVE IT IN");
//                assertEquals(PlayerStatus.LOST, match.getGame().getPlayers().get(0).getStatus());
//            }
//            assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            wentThroughEveryScenario.put("All players stick, verdict is out", true);
//            sendChangedRound(match);
//            assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//            for (Player player : match.getGame().getPlayers()) {
//                assertEquals(PlayerStatus.BETTING, player.getStatus());
//            }
//            continue;
//        }
//

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }


    @Test
    void onePlayerOneDealer_PlayerDropsOutBeforeStartAnotherPlayerGetsInAndPlays() {
        Helper3 helper3 = new Helper3();
        helper3.removeCustomAssertion(CustomAssertions.PLAYER2_HAD_BJ_FROM_FIRST_HAND);
        helper3.removeCustomAssertion(CustomAssertions.PLAYER2_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);

        for (int i = 0; i < 100000; i++) {
//            System.out.println( i );
            user1 = new User("aa", "aa", "aa");
            user1.setMoney(100000);

            user7 = new User("ww", "ww", "ww");
            user7.setMoney(100000);
            match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
            helper3.setMatch(match);
            helper3.addDealerToMatch(user7);
            helper3.addUserToMatch(user1, 2);

            helper3.playerLeaves(match.getGame().getPlayers().get(0));
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            helper3.addUserToMatch(user2, 2);



            helper3.startGame();
            Dealer dealer = match.getGame().getDealer();
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
            Player player2 = match.getGame().getPlayerByEmail(user2.getEmail());
            helper3.playerBet(bet2);

            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
//            // if the playerWhoJustGotDealtBJ is not null, then our player had blackjack
//            // we then change the turn and turn playerWhoJustGotDealtBJ to null again.
//            // at this point the game can have taken lot's of different directions.
//            // the sendChangeTurn will keep running until the next player doesn't have BJ
//            // that means that 3-4 players can all have BJ, and the next turn goes to the 5 player
//            // we amend for that by having and if statement and checking the status of each player
//            // from now on. If they all had black jack, the round should be through and the players
//            // in a betting round
//            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                wentThroughEveryScenario.put("Player1 had BJ from first hand", true);
//                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                sendChangedTurn(match);
//                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            }
//
//            // if the bet has reset, that means the game is over (all had BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("All had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//            if (player1.getStatus() != PlayerStatus.BLACKJACK) { // the player didn't have BJ
//                wentThroughEveryScenario.put("Player1 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player1.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Player3 got BJ from first hand", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == player3);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                }
//            }
//
//            // if the bet has reset, that means the game is over (player3 and dealer BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("Player3 and dealer had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//
//            if (player3.getStatus() != PlayerStatus.BLACKJACK) { // the player3 didn't have BJ
//                wentThroughEveryScenario.put("Player3 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player3.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player3.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Dealer got BJ from first hand,  game was reset", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == dealer);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                    assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                    for (Player player : match.getGame().getPlayers()) {
//                        assertEquals(PlayerStatus.BETTING, player.getStatus());
//                    }
//                    continue;
//                }
//            }
//
//
//
//
//
//            // the next player, dealer in that case, should only be in a playing status, since if they had
//            // bj, the game would been through.
//            // they decide to stick. then, verdict should also be out, since
//            // the last player who his turn finished was the dealer. The dealer doesn't get
//            // neither LOST or WON status from the verdict, since he might won in one turn and lost in another
//            // so, the dealer has the same status as his last choice: STICK in that case.
//            // The player on the other hand, either have a won or a lost status.
//            assertTrue(dealer.getStatus() == PlayerStatus.PLAYING);
//            match.getGame().sticks();
//            assertTrue(match.getGame().isVerdictOut());
//            assertTrue(dealer.getStatus() == PlayerStatus.STICK);
//            assertTrue(player1.getStatus() == PlayerStatus.WON ||
//                    player1.getStatus() == PlayerStatus.LOST);
//            assertTrue(player3.getStatus() == PlayerStatus.WON ||
//                    player3.getStatus() == PlayerStatus.LOST);
//            if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
//                fail("WE SHOULD NOT BE HERE SINCE IF THE DEALER HAD ALSO BJ THE GAME SHOULD BE THROUGH ALREADY" +
//                        "BUT THIS MIGHT BE RELEVANT TO OTHER TESTS SO I LEAVE IT IN");
//                assertEquals(PlayerStatus.LOST, match.getGame().getPlayers().get(0).getStatus());
//            }
//            assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            wentThroughEveryScenario.put("All players stick, verdict is out", true);
//            sendChangedRound(match);
//            assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//            for (Player player : match.getGame().getPlayers()) {
//                assertEquals(PlayerStatus.BETTING, player.getStatus());
//            }
//            continue;
//        }
//

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    // 2 PLAYERS, DEALER LEAVES

    @Test
    void onePlayerOneDealer_DealerDropsOutAfterPlayersBettingAnotherPlayerGetsInAndPlays() {
        Helper3 helper3 = new Helper3();
        helper3.removeCustomAssertion(CustomAssertions.PLAYER2_HAD_BJ_FROM_FIRST_HAND);
        helper3.removeCustomAssertion(CustomAssertions.PLAYER2_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        for (int i = 0; i < 100000; i++) {
//            System.out.println( i );
            user1 = new User("aa", "aa", "aa");
            user1.setMoney(100000);
            user7 = new User("ww", "ww", "ww");
            user7.setMoney(100000);
            match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
            helper3.setMatch(match);
            helper3.addDealerToMatch(user7);
            helper3.addUserToMatch(user1, 2);

            helper3.startGame();
            Player player1 = match.getGame().getPlayers().get(0);
            Dealer dealer = match.getGame().getDealer();
            helper3.playerBet(bet1);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();

            helper3.playerLeaves(dealer);
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            helper3.addUserToMatch(user2, 2);


            helper3.startGame();
            Player newPlayer1 = match.getGame().getPlayers().get(0);
            Dealer dealer2 = match.getGame().getDealer();
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();



//            // if the playerWhoJustGotDealtBJ is not null, then our player had blackjack
//            // we then change the turn and turn playerWhoJustGotDealtBJ to null again.
//            // at this point the game can have taken lot's of different directions.
//            // the sendChangeTurn will keep running until the next player doesn't have BJ
//            // that means that 3-4 players can all have BJ, and the next turn goes to the 5 player
//            // we amend for that by having and if statement and checking the status of each player
//            // from now on. If they all had black jack, the round should be through and the players
//            // in a betting round
//            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                wentThroughEveryScenario.put("Player1 had BJ from first hand", true);
//                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                sendChangedTurn(match);
//                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            }
//
//            // if the bet has reset, that means the game is over (all had BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("All had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//            if (player1.getStatus() != PlayerStatus.BLACKJACK) { // the player didn't have BJ
//                wentThroughEveryScenario.put("Player1 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player1.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Player3 got BJ from first hand", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == player3);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                }
//            }
//
//            // if the bet has reset, that means the game is over (player3 and dealer BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("Player3 and dealer had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//
//            if (player3.getStatus() != PlayerStatus.BLACKJACK) { // the player3 didn't have BJ
//                wentThroughEveryScenario.put("Player3 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player3.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player3.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Dealer got BJ from first hand,  game was reset", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == dealer);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                    assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                    for (Player player : match.getGame().getPlayers()) {
//                        assertEquals(PlayerStatus.BETTING, player.getStatus());
//                    }
//                    continue;
//                }
//            }
//
//
//
//
//
//            // the next player, dealer in that case, should only be in a playing status, since if they had
//            // bj, the game would been through.
//            // they decide to stick. then, verdict should also be out, since
//            // the last player who his turn finished was the dealer. The dealer doesn't get
//            // neither LOST or WON status from the verdict, since he might won in one turn and lost in another
//            // so, the dealer has the same status as his last choice: STICK in that case.
//            // The player on the other hand, either have a won or a lost status.
//            assertTrue(dealer.getStatus() == PlayerStatus.PLAYING);
//            match.getGame().sticks();
//            assertTrue(match.getGame().isVerdictOut());
//            assertTrue(dealer.getStatus() == PlayerStatus.STICK);
//            assertTrue(player1.getStatus() == PlayerStatus.WON ||
//                    player1.getStatus() == PlayerStatus.LOST);
//            assertTrue(player3.getStatus() == PlayerStatus.WON ||
//                    player3.getStatus() == PlayerStatus.LOST);
//            if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
//                fail("WE SHOULD NOT BE HERE SINCE IF THE DEALER HAD ALSO BJ THE GAME SHOULD BE THROUGH ALREADY" +
//                        "BUT THIS MIGHT BE RELEVANT TO OTHER TESTS SO I LEAVE IT IN");
//                assertEquals(PlayerStatus.LOST, match.getGame().getPlayers().get(0).getStatus());
//            }
//            assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            wentThroughEveryScenario.put("All players stick, verdict is out", true);
//            sendChangedRound(match);
//            assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//            for (Player player : match.getGame().getPlayers()) {
//                assertEquals(PlayerStatus.BETTING, player.getStatus());
//            }
//            continue;
//        }
//

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    @Test
    void onePlayerOneDealer_DealerDropsOutBeforePlayersBettingAnotherPlayerGetsInAndPlays() {
        Helper3 helper3 = new Helper3();
        helper3.removeCustomAssertion(CustomAssertions.PLAYER2_HAD_BJ_FROM_FIRST_HAND);
        helper3.removeCustomAssertion(CustomAssertions.PLAYER2_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        for (int i = 0; i < 100000; i++) {
//            System.out.println( i );
            user1 = new User("aa", "aa", "aa");
            user1.setMoney(100000);
            user7 = new User("ww", "ww", "ww");
            user7.setMoney(100000);
            match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
            helper3.setMatch(match);
            helper3.addDealerToMatch(user7);
            helper3.addUserToMatch(user1, 2);

            helper3.startGame();
            Player player1 = match.getGame().getPlayers().get(0);
            Dealer dealer = match.getGame().getDealer();

            helper3.playerLeaves(dealer);
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            helper3.addUserToMatch(user2, 2);


            helper3.startGame();
            Player newPlayer1 = match.getGame().getPlayers().get(0);
            Dealer dealer2 = match.getGame().getDealer();
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();



//            // if the playerWhoJustGotDealtBJ is not null, then our player had blackjack
//            // we then change the turn and turn playerWhoJustGotDealtBJ to null again.
//            // at this point the game can have taken lot's of different directions.
//            // the sendChangeTurn will keep running until the next player doesn't have BJ
//            // that means that 3-4 players can all have BJ, and the next turn goes to the 5 player
//            // we amend for that by having and if statement and checking the status of each player
//            // from now on. If they all had black jack, the round should be through and the players
//            // in a betting round
//            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                wentThroughEveryScenario.put("Player1 had BJ from first hand", true);
//                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                sendChangedTurn(match);
//                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            }
//
//            // if the bet has reset, that means the game is over (all had BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("All had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//            if (player1.getStatus() != PlayerStatus.BLACKJACK) { // the player didn't have BJ
//                wentThroughEveryScenario.put("Player1 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player1.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Player3 got BJ from first hand", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == player3);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                }
//            }
//
//            // if the bet has reset, that means the game is over (player3 and dealer BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("Player3 and dealer had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//
//            if (player3.getStatus() != PlayerStatus.BLACKJACK) { // the player3 didn't have BJ
//                wentThroughEveryScenario.put("Player3 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player3.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player3.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Dealer got BJ from first hand,  game was reset", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == dealer);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                    assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                    for (Player player : match.getGame().getPlayers()) {
//                        assertEquals(PlayerStatus.BETTING, player.getStatus());
//                    }
//                    continue;
//                }
//            }
//
//
//
//
//
//            // the next player, dealer in that case, should only be in a playing status, since if they had
//            // bj, the game would been through.
//            // they decide to stick. then, verdict should also be out, since
//            // the last player who his turn finished was the dealer. The dealer doesn't get
//            // neither LOST or WON status from the verdict, since he might won in one turn and lost in another
//            // so, the dealer has the same status as his last choice: STICK in that case.
//            // The player on the other hand, either have a won or a lost status.
//            assertTrue(dealer.getStatus() == PlayerStatus.PLAYING);
//            match.getGame().sticks();
//            assertTrue(match.getGame().isVerdictOut());
//            assertTrue(dealer.getStatus() == PlayerStatus.STICK);
//            assertTrue(player1.getStatus() == PlayerStatus.WON ||
//                    player1.getStatus() == PlayerStatus.LOST);
//            assertTrue(player3.getStatus() == PlayerStatus.WON ||
//                    player3.getStatus() == PlayerStatus.LOST);
//            if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
//                fail("WE SHOULD NOT BE HERE SINCE IF THE DEALER HAD ALSO BJ THE GAME SHOULD BE THROUGH ALREADY" +
//                        "BUT THIS MIGHT BE RELEVANT TO OTHER TESTS SO I LEAVE IT IN");
//                assertEquals(PlayerStatus.LOST, match.getGame().getPlayers().get(0).getStatus());
//            }
//            assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            wentThroughEveryScenario.put("All players stick, verdict is out", true);
//            sendChangedRound(match);
//            assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//            for (Player player : match.getGame().getPlayers()) {
//                assertEquals(PlayerStatus.BETTING, player.getStatus());
//            }
//            continue;
//        }
//

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    @Test
    void onePlayerOneDealer_DealerDropsOutBeforeStartAnotherPlayerGetsInAndPlays() {
        Helper3 helper3 = new Helper3();
        helper3.removeCustomAssertion(CustomAssertions.PLAYER2_HAD_BJ_FROM_FIRST_HAND);
        helper3.removeCustomAssertion(CustomAssertions.PLAYER2_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        for (int i = 0; i < 100000; i++) {
//            System.out.println( i );
            user1 = new User("aa", "aa", "aa");
            user1.setMoney(100000);
            user7 = new User("ww", "ww", "ww");
            user7.setMoney(100000);
            match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
            helper3.setMatch(match);
            helper3.addDealerToMatch(user7);
            helper3.addUserToMatch(user1, 2);

            helper3.playerLeaves(match.getGame().getDealer());
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            helper3.addUserToMatch(user2, 2);


            helper3.startGame();
            Player newPlayer1 = match.getGame().getPlayers().get(0);
            Dealer dealer2 = match.getGame().getDealer();
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();



//            // if the playerWhoJustGotDealtBJ is not null, then our player had blackjack
//            // we then change the turn and turn playerWhoJustGotDealtBJ to null again.
//            // at this point the game can have taken lot's of different directions.
//            // the sendChangeTurn will keep running until the next player doesn't have BJ
//            // that means that 3-4 players can all have BJ, and the next turn goes to the 5 player
//            // we amend for that by having and if statement and checking the status of each player
//            // from now on. If they all had black jack, the round should be through and the players
//            // in a betting round
//            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                wentThroughEveryScenario.put("Player1 had BJ from first hand", true);
//                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                sendChangedTurn(match);
//                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            }
//
//            // if the bet has reset, that means the game is over (all had BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("All had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//            if (player1.getStatus() != PlayerStatus.BLACKJACK) { // the player didn't have BJ
//                wentThroughEveryScenario.put("Player1 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player1.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Player3 got BJ from first hand", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == player3);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                }
//            }
//
//            // if the bet has reset, that means the game is over (player3 and dealer BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("Player3 and dealer had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//
//            if (player3.getStatus() != PlayerStatus.BLACKJACK) { // the player3 didn't have BJ
//                wentThroughEveryScenario.put("Player3 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player3.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player3.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Dealer got BJ from first hand,  game was reset", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == dealer);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                    assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                    for (Player player : match.getGame().getPlayers()) {
//                        assertEquals(PlayerStatus.BETTING, player.getStatus());
//                    }
//                    continue;
//                }
//            }
//
//
//
//
//
//            // the next player, dealer in that case, should only be in a playing status, since if they had
//            // bj, the game would been through.
//            // they decide to stick. then, verdict should also be out, since
//            // the last player who his turn finished was the dealer. The dealer doesn't get
//            // neither LOST or WON status from the verdict, since he might won in one turn and lost in another
//            // so, the dealer has the same status as his last choice: STICK in that case.
//            // The player on the other hand, either have a won or a lost status.
//            assertTrue(dealer.getStatus() == PlayerStatus.PLAYING);
//            match.getGame().sticks();
//            assertTrue(match.getGame().isVerdictOut());
//            assertTrue(dealer.getStatus() == PlayerStatus.STICK);
//            assertTrue(player1.getStatus() == PlayerStatus.WON ||
//                    player1.getStatus() == PlayerStatus.LOST);
//            assertTrue(player3.getStatus() == PlayerStatus.WON ||
//                    player3.getStatus() == PlayerStatus.LOST);
//            if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
//                fail("WE SHOULD NOT BE HERE SINCE IF THE DEALER HAD ALSO BJ THE GAME SHOULD BE THROUGH ALREADY" +
//                        "BUT THIS MIGHT BE RELEVANT TO OTHER TESTS SO I LEAVE IT IN");
//                assertEquals(PlayerStatus.LOST, match.getGame().getPlayers().get(0).getStatus());
//            }
//            assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            wentThroughEveryScenario.put("All players stick, verdict is out", true);
//            sendChangedRound(match);
//            assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//            for (Player player : match.getGame().getPlayers()) {
//                assertEquals(PlayerStatus.BETTING, player.getStatus());
//            }
//            continue;
//        }
//

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    // 3 PLAYERS
    @Test
    void shouldBeAbleToPlayWithThreeUsers() {
        Helper3 helper3 = new Helper3();

        for (int i = 0; i < 100000; i++) {
//            System.out.println( i );
            user1 = new User("aa", "aa", "aa");
            user1.setMoney(100000);
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            user7 = new User("ww", "ww", "ww");
            user7.setMoney(100000);
            match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
            helper3.setMatch(match);
            helper3.addDealerToMatch(user7);
            helper3.addUserToMatch(user1, 2);
            helper3.addUserToMatch(user2, 3);

            helper3.startGame();
            Player player1 = match.getGame().getPlayers().get(0);
            Player player2 = match.getGame().getPlayers().get(1);
            Dealer dealer = match.getGame().getDealer();
            helper3.playerBet(bet1);
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();



//            // if the playerWhoJustGotDealtBJ is not null, then our player had blackjack
//            // we then change the turn and turn playerWhoJustGotDealtBJ to null again.
//            // at this point the game can have taken lot's of different directions.
//            // the sendChangeTurn will keep running until the next player doesn't have BJ
//            // that means that 3-4 players can all have BJ, and the next turn goes to the 5 player
//            // we amend for that by having and if statement and checking the status of each player
//            // from now on. If they all had black jack, the round should be through and the players
//            // in a betting round
//            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                wentThroughEveryScenario.put("Player1 had BJ from first hand", true);
//                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                sendChangedTurn(match);
//                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            }
//
//            // if the bet has reset, that means the game is over (all had BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("All had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//            if (player1.getStatus() != PlayerStatus.BLACKJACK) { // the player didn't have BJ
//                wentThroughEveryScenario.put("Player1 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player1.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Player3 got BJ from first hand", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == player3);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                }
//            }
//
//            // if the bet has reset, that means the game is over (player3 and dealer BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("Player3 and dealer had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//
//            if (player3.getStatus() != PlayerStatus.BLACKJACK) { // the player3 didn't have BJ
//                wentThroughEveryScenario.put("Player3 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player3.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player3.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Dealer got BJ from first hand,  game was reset", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == dealer);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                    assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                    for (Player player : match.getGame().getPlayers()) {
//                        assertEquals(PlayerStatus.BETTING, player.getStatus());
//                    }
//                    continue;
//                }
//            }
//
//
//
//
//
//            // the next player, dealer in that case, should only be in a playing status, since if they had
//            // bj, the game would been through.
//            // they decide to stick. then, verdict should also be out, since
//            // the last player who his turn finished was the dealer. The dealer doesn't get
//            // neither LOST or WON status from the verdict, since he might won in one turn and lost in another
//            // so, the dealer has the same status as his last choice: STICK in that case.
//            // The player on the other hand, either have a won or a lost status.
//            assertTrue(dealer.getStatus() == PlayerStatus.PLAYING);
//            match.getGame().sticks();
//            assertTrue(match.getGame().isVerdictOut());
//            assertTrue(dealer.getStatus() == PlayerStatus.STICK);
//            assertTrue(player1.getStatus() == PlayerStatus.WON ||
//                    player1.getStatus() == PlayerStatus.LOST);
//            assertTrue(player3.getStatus() == PlayerStatus.WON ||
//                    player3.getStatus() == PlayerStatus.LOST);
//            if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
//                fail("WE SHOULD NOT BE HERE SINCE IF THE DEALER HAD ALSO BJ THE GAME SHOULD BE THROUGH ALREADY" +
//                        "BUT THIS MIGHT BE RELEVANT TO OTHER TESTS SO I LEAVE IT IN");
//                assertEquals(PlayerStatus.LOST, match.getGame().getPlayers().get(0).getStatus());
//            }
//            assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            wentThroughEveryScenario.put("All players stick, verdict is out", true);
//            sendChangedRound(match);
//            assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//            for (Player player : match.getGame().getPlayers()) {
//                assertEquals(PlayerStatus.BETTING, player.getStatus());
//            }
//            continue;
//        }
//

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }


    // 3 PLAYERS, PLAYER LEAVES
    @Test
    void threePlayersOneDealer_Player1DropsOutAfterBettingAnotherPlayerGetsInAndPlays() {
        Helper3 helper3 = new Helper3();

        for (int i = 0; i < 100000; i++) {
//            System.out.println( i );
            user1 = new User("aa", "aa", "aa");
            user1.setMoney(100000);
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            user7 = new User("ww", "ww", "ww");
            user7.setMoney(100000);
            match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
            helper3.setMatch(match);
            helper3.addDealerToMatch(user7);
            helper3.addUserToMatch(user1, 2);
            helper3.addUserToMatch(user2, 3);

            helper3.startGame();
            Player player1 = match.getGame().getPlayers().get(0);
            Player player2 = match.getGame().getPlayers().get(1);
            Dealer dealer = match.getGame().getDealer();
            helper3.playerBet(bet1);
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();

            helper3.playerLeaves(player1);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);

//            helper3.playerTurn("PLAYER1");
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
//            // if the playerWhoJustGotDealtBJ is not null, then our player had blackjack
//            // we then change the turn and turn playerWhoJustGotDealtBJ to null again.
//            // at this point the game can have taken lot's of different directions.
//            // the sendChangeTurn will keep running until the next player doesn't have BJ
//            // that means that 3-4 players can all have BJ, and the next turn goes to the 5 player
//            // we amend for that by having and if statement and checking the status of each player
//            // from now on. If they all had black jack, the round should be through and the players
//            // in a betting round
//            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                wentThroughEveryScenario.put("Player1 had BJ from first hand", true);
//                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                sendChangedTurn(match);
//                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            }
//
//            // if the bet has reset, that means the game is over (all had BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("All had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//            if (player1.getStatus() != PlayerStatus.BLACKJACK) { // the player didn't have BJ
//                wentThroughEveryScenario.put("Player1 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player1.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Player3 got BJ from first hand", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == player3);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                }
//            }
//
//            // if the bet has reset, that means the game is over (player3 and dealer BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("Player3 and dealer had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//
//            if (player3.getStatus() != PlayerStatus.BLACKJACK) { // the player3 didn't have BJ
//                wentThroughEveryScenario.put("Player3 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player3.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player3.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Dealer got BJ from first hand,  game was reset", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == dealer);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                    assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                    for (Player player : match.getGame().getPlayers()) {
//                        assertEquals(PlayerStatus.BETTING, player.getStatus());
//                    }
//                    continue;
//                }
//            }
//
//
//
//
//
//            // the next player, dealer in that case, should only be in a playing status, since if they had
//            // bj, the game would been through.
//            // they decide to stick. then, verdict should also be out, since
//            // the last player who his turn finished was the dealer. The dealer doesn't get
//            // neither LOST or WON status from the verdict, since he might won in one turn and lost in another
//            // so, the dealer has the same status as his last choice: STICK in that case.
//            // The player on the other hand, either have a won or a lost status.
//            assertTrue(dealer.getStatus() == PlayerStatus.PLAYING);
//            match.getGame().sticks();
//            assertTrue(match.getGame().isVerdictOut());
//            assertTrue(dealer.getStatus() == PlayerStatus.STICK);
//            assertTrue(player1.getStatus() == PlayerStatus.WON ||
//                    player1.getStatus() == PlayerStatus.LOST);
//            assertTrue(player3.getStatus() == PlayerStatus.WON ||
//                    player3.getStatus() == PlayerStatus.LOST);
//            if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
//                fail("WE SHOULD NOT BE HERE SINCE IF THE DEALER HAD ALSO BJ THE GAME SHOULD BE THROUGH ALREADY" +
//                        "BUT THIS MIGHT BE RELEVANT TO OTHER TESTS SO I LEAVE IT IN");
//                assertEquals(PlayerStatus.LOST, match.getGame().getPlayers().get(0).getStatus());
//            }
//            assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            wentThroughEveryScenario.put("All players stick, verdict is out", true);
//            sendChangedRound(match);
//            assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//            for (Player player : match.getGame().getPlayers()) {
//                assertEquals(PlayerStatus.BETTING, player.getStatus());
//            }
//            continue;
//        }
//

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    @Test
    void threePlayersOneDealer_Player1DropsOutBeforeBettingAnotherPlayerGetsInAndPlays() {
        Helper3 helper3 = new Helper3();

        for (int i = 0; i < 100000; i++) {
//            System.out.println( i );
            user1 = new User("aa", "aa", "aa");
            user1.setMoney(100000);
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            user7 = new User("ww", "ww", "ww");
            user7.setMoney(100000);
            match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
            helper3.setMatch(match);
            helper3.addDealerToMatch(user7);
            helper3.addUserToMatch(user1, 2);
            helper3.addUserToMatch(user2, 3);

            helper3.startGame();
            Player player1 = match.getGame().getPlayers().get(0);
            Player player2 = match.getGame().getPlayers().get(1);
            Dealer dealer = match.getGame().getDealer();

            helper3.playerLeaves(player1);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);

            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
//            helper3.playerTurn("PLAYER1");
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
//            // if the playerWhoJustGotDealtBJ is not null, then our player had blackjack
//            // we then change the turn and turn playerWhoJustGotDealtBJ to null again.
//            // at this point the game can have taken lot's of different directions.
//            // the sendChangeTurn will keep running until the next player doesn't have BJ
//            // that means that 3-4 players can all have BJ, and the next turn goes to the 5 player
//            // we amend for that by having and if statement and checking the status of each player
//            // from now on. If they all had black jack, the round should be through and the players
//            // in a betting round
//            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                wentThroughEveryScenario.put("Player1 had BJ from first hand", true);
//                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                sendChangedTurn(match);
//                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            }
//
//            // if the bet has reset, that means the game is over (all had BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("All had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//            if (player1.getStatus() != PlayerStatus.BLACKJACK) { // the player didn't have BJ
//                wentThroughEveryScenario.put("Player1 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player1.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Player3 got BJ from first hand", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == player3);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                }
//            }
//
//            // if the bet has reset, that means the game is over (player3 and dealer BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("Player3 and dealer had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//
//            if (player3.getStatus() != PlayerStatus.BLACKJACK) { // the player3 didn't have BJ
//                wentThroughEveryScenario.put("Player3 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player3.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player3.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Dealer got BJ from first hand,  game was reset", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == dealer);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                    assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                    for (Player player : match.getGame().getPlayers()) {
//                        assertEquals(PlayerStatus.BETTING, player.getStatus());
//                    }
//                    continue;
//                }
//            }
//
//
//
//
//
//            // the next player, dealer in that case, should only be in a playing status, since if they had
//            // bj, the game would been through.
//            // they decide to stick. then, verdict should also be out, since
//            // the last player who his turn finished was the dealer. The dealer doesn't get
//            // neither LOST or WON status from the verdict, since he might won in one turn and lost in another
//            // so, the dealer has the same status as his last choice: STICK in that case.
//            // The player on the other hand, either have a won or a lost status.
//            assertTrue(dealer.getStatus() == PlayerStatus.PLAYING);
//            match.getGame().sticks();
//            assertTrue(match.getGame().isVerdictOut());
//            assertTrue(dealer.getStatus() == PlayerStatus.STICK);
//            assertTrue(player1.getStatus() == PlayerStatus.WON ||
//                    player1.getStatus() == PlayerStatus.LOST);
//            assertTrue(player3.getStatus() == PlayerStatus.WON ||
//                    player3.getStatus() == PlayerStatus.LOST);
//            if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
//                fail("WE SHOULD NOT BE HERE SINCE IF THE DEALER HAD ALSO BJ THE GAME SHOULD BE THROUGH ALREADY" +
//                        "BUT THIS MIGHT BE RELEVANT TO OTHER TESTS SO I LEAVE IT IN");
//                assertEquals(PlayerStatus.LOST, match.getGame().getPlayers().get(0).getStatus());
//            }
//            assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            wentThroughEveryScenario.put("All players stick, verdict is out", true);
//            sendChangedRound(match);
//            assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//            for (Player player : match.getGame().getPlayers()) {
//                assertEquals(PlayerStatus.BETTING, player.getStatus());
//            }
//            continue;
//        }
//

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    @Test
    void threePlayersOneDealer_Player1DropsOutRightBeforeBettingAnotherPlayerGetsInAndPlays() {
        Helper3 helper3 = new Helper3();

        for (int i = 0; i < 100000; i++) {
//            System.out.println( i );
            user1 = new User("aa", "aa", "aa");
            user1.setMoney(100000);
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            user7 = new User("ww", "ww", "ww");
            user7.setMoney(100000);
            match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
            helper3.setMatch(match);
            helper3.addDealerToMatch(user7);
            helper3.addUserToMatch(user1, 2);
            helper3.addUserToMatch(user2, 3);

            helper3.startGame();
            Player player1 = match.getGame().getPlayers().get(0);
            Player player2 = match.getGame().getPlayers().get(1);
            Dealer dealer = match.getGame().getDealer();
            helper3.playerBet(bet2);

            helper3.playerLeaves(player1);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);

            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
//            helper3.playerTurn("PLAYER1");
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
//            // if the playerWhoJustGotDealtBJ is not null, then our player had blackjack
//            // we then change the turn and turn playerWhoJustGotDealtBJ to null again.
//            // at this point the game can have taken lot's of different directions.
//            // the sendChangeTurn will keep running until the next player doesn't have BJ
//            // that means that 3-4 players can all have BJ, and the next turn goes to the 5 player
//            // we amend for that by having and if statement and checking the status of each player
//            // from now on. If they all had black jack, the round should be through and the players
//            // in a betting round
//            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                wentThroughEveryScenario.put("Player1 had BJ from first hand", true);
//                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                sendChangedTurn(match);
//                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            }
//
//            // if the bet has reset, that means the game is over (all had BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("All had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//            if (player1.getStatus() != PlayerStatus.BLACKJACK) { // the player didn't have BJ
//                wentThroughEveryScenario.put("Player1 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player1.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Player3 got BJ from first hand", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == player3);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                }
//            }
//
//            // if the bet has reset, that means the game is over (player3 and dealer BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("Player3 and dealer had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//
//            if (player3.getStatus() != PlayerStatus.BLACKJACK) { // the player3 didn't have BJ
//                wentThroughEveryScenario.put("Player3 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player3.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player3.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Dealer got BJ from first hand,  game was reset", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == dealer);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                    assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                    for (Player player : match.getGame().getPlayers()) {
//                        assertEquals(PlayerStatus.BETTING, player.getStatus());
//                    }
//                    continue;
//                }
//            }
//
//
//
//
//
//            // the next player, dealer in that case, should only be in a playing status, since if they had
//            // bj, the game would been through.
//            // they decide to stick. then, verdict should also be out, since
//            // the last player who his turn finished was the dealer. The dealer doesn't get
//            // neither LOST or WON status from the verdict, since he might won in one turn and lost in another
//            // so, the dealer has the same status as his last choice: STICK in that case.
//            // The player on the other hand, either have a won or a lost status.
//            assertTrue(dealer.getStatus() == PlayerStatus.PLAYING);
//            match.getGame().sticks();
//            assertTrue(match.getGame().isVerdictOut());
//            assertTrue(dealer.getStatus() == PlayerStatus.STICK);
//            assertTrue(player1.getStatus() == PlayerStatus.WON ||
//                    player1.getStatus() == PlayerStatus.LOST);
//            assertTrue(player3.getStatus() == PlayerStatus.WON ||
//                    player3.getStatus() == PlayerStatus.LOST);
//            if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
//                fail("WE SHOULD NOT BE HERE SINCE IF THE DEALER HAD ALSO BJ THE GAME SHOULD BE THROUGH ALREADY" +
//                        "BUT THIS MIGHT BE RELEVANT TO OTHER TESTS SO I LEAVE IT IN");
//                assertEquals(PlayerStatus.LOST, match.getGame().getPlayers().get(0).getStatus());
//            }
//            assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            wentThroughEveryScenario.put("All players stick, verdict is out", true);
//            sendChangedRound(match);
//            assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//            for (Player player : match.getGame().getPlayers()) {
//                assertEquals(PlayerStatus.BETTING, player.getStatus());
//            }
//            continue;
//        }
//

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    @Test
    void threePlayersOneDealer_Player1DropsOutBeforePlayer2BetsAnotherPlayerGetsInAndPlays() {
        Helper3 helper3 = new Helper3();

        for (int i = 0; i < 100000; i++) {
//            System.out.println( i );
            user1 = new User("aa", "aa", "aa");
            user1.setMoney(100000);
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            user7 = new User("ww", "ww", "ww");
            user7.setMoney(100000);
            match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
            helper3.setMatch(match);
            helper3.addDealerToMatch(user7);
            helper3.addUserToMatch(user1, 2);
            helper3.addUserToMatch(user2, 3);

            helper3.startGame();
            Player player1 = match.getGame().getPlayers().get(0);
            Player player2 = match.getGame().getPlayers().get(1);
            Dealer dealer = match.getGame().getDealer();
            helper3.playerBet(bet1);

            helper3.playerLeaves(player1);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);

            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();

//            helper3.playerTurn("PLAYER1");
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
//            // if the playerWhoJustGotDealtBJ is not null, then our player had blackjack
//            // we then change the turn and turn playerWhoJustGotDealtBJ to null again.
//            // at this point the game can have taken lot's of different directions.
//            // the sendChangeTurn will keep running until the next player doesn't have BJ
//            // that means that 3-4 players can all have BJ, and the next turn goes to the 5 player
//            // we amend for that by having and if statement and checking the status of each player
//            // from now on. If they all had black jack, the round should be through and the players
//            // in a betting round
//            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                wentThroughEveryScenario.put("Player1 had BJ from first hand", true);
//                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                sendChangedTurn(match);
//                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            }
//
//            // if the bet has reset, that means the game is over (all had BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("All had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//            if (player1.getStatus() != PlayerStatus.BLACKJACK) { // the player didn't have BJ
//                wentThroughEveryScenario.put("Player1 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player1.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Player3 got BJ from first hand", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == player3);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                }
//            }
//
//            // if the bet has reset, that means the game is over (player3 and dealer BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("Player3 and dealer had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//
//            if (player3.getStatus() != PlayerStatus.BLACKJACK) { // the player3 didn't have BJ
//                wentThroughEveryScenario.put("Player3 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player3.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player3.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Dealer got BJ from first hand,  game was reset", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == dealer);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                    assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                    for (Player player : match.getGame().getPlayers()) {
//                        assertEquals(PlayerStatus.BETTING, player.getStatus());
//                    }
//                    continue;
//                }
//            }
//
//
//
//
//
//            // the next player, dealer in that case, should only be in a playing status, since if they had
//            // bj, the game would been through.
//            // they decide to stick. then, verdict should also be out, since
//            // the last player who his turn finished was the dealer. The dealer doesn't get
//            // neither LOST or WON status from the verdict, since he might won in one turn and lost in another
//            // so, the dealer has the same status as his last choice: STICK in that case.
//            // The player on the other hand, either have a won or a lost status.
//            assertTrue(dealer.getStatus() == PlayerStatus.PLAYING);
//            match.getGame().sticks();
//            assertTrue(match.getGame().isVerdictOut());
//            assertTrue(dealer.getStatus() == PlayerStatus.STICK);
//            assertTrue(player1.getStatus() == PlayerStatus.WON ||
//                    player1.getStatus() == PlayerStatus.LOST);
//            assertTrue(player3.getStatus() == PlayerStatus.WON ||
//                    player3.getStatus() == PlayerStatus.LOST);
//            if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
//                fail("WE SHOULD NOT BE HERE SINCE IF THE DEALER HAD ALSO BJ THE GAME SHOULD BE THROUGH ALREADY" +
//                        "BUT THIS MIGHT BE RELEVANT TO OTHER TESTS SO I LEAVE IT IN");
//                assertEquals(PlayerStatus.LOST, match.getGame().getPlayers().get(0).getStatus());
//            }
//            assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            wentThroughEveryScenario.put("All players stick, verdict is out", true);
//            sendChangedRound(match);
//            assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//            for (Player player : match.getGame().getPlayers()) {
//                assertEquals(PlayerStatus.BETTING, player.getStatus());
//            }
//            continue;
//        }
//

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    @Test
    void threePlayersOneDealer_Player1DropsOutAfterPlayingAnotherPlayerGetsInAndPlays() {
        Helper3 helper3 = new Helper3();

        for (int i = 0; i < 100000; i++) {
//            System.out.println( i );
            user1 = new User("aa", "aa", "aa");
            user1.setMoney(100000);
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            user7 = new User("ww", "ww", "ww");
            user7.setMoney(100000);
            match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
            helper3.setMatch(match);
            helper3.addDealerToMatch(user7);
            helper3.addUserToMatch(user1, 2);
            helper3.addUserToMatch(user2, 3);

            helper3.startGame();
            Player player1 = match.getGame().getPlayers().get(0);
            Player player2 = match.getGame().getPlayers().get(1);
            Dealer dealer = match.getGame().getDealer();
            helper3.playerBet(bet1);
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();


            helper3.playerLeaves(player1);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);

            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
//            // if the playerWhoJustGotDealtBJ is not null, then our player had blackjack
//            // we then change the turn and turn playerWhoJustGotDealtBJ to null again.
//            // at this point the game can have taken lot's of different directions.
//            // the sendChangeTurn will keep running until the next player doesn't have BJ
//            // that means that 3-4 players can all have BJ, and the next turn goes to the 5 player
//            // we amend for that by having and if statement and checking the status of each player
//            // from now on. If they all had black jack, the round should be through and the players
//            // in a betting round
//            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                wentThroughEveryScenario.put("Player1 had BJ from first hand", true);
//                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                sendChangedTurn(match);
//                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            }
//
//            // if the bet has reset, that means the game is over (all had BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("All had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//            if (player1.getStatus() != PlayerStatus.BLACKJACK) { // the player didn't have BJ
//                wentThroughEveryScenario.put("Player1 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player1.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Player3 got BJ from first hand", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == player3);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                }
//            }
//
//            // if the bet has reset, that means the game is over (player3 and dealer BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("Player3 and dealer had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//
//            if (player3.getStatus() != PlayerStatus.BLACKJACK) { // the player3 didn't have BJ
//                wentThroughEveryScenario.put("Player3 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player3.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player3.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Dealer got BJ from first hand,  game was reset", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == dealer);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                    assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                    for (Player player : match.getGame().getPlayers()) {
//                        assertEquals(PlayerStatus.BETTING, player.getStatus());
//                    }
//                    continue;
//                }
//            }
//
//
//
//
//
//            // the next player, dealer in that case, should only be in a playing status, since if they had
//            // bj, the game would been through.
//            // they decide to stick. then, verdict should also be out, since
//            // the last player who his turn finished was the dealer. The dealer doesn't get
//            // neither LOST or WON status from the verdict, since he might won in one turn and lost in another
//            // so, the dealer has the same status as his last choice: STICK in that case.
//            // The player on the other hand, either have a won or a lost status.
//            assertTrue(dealer.getStatus() == PlayerStatus.PLAYING);
//            match.getGame().sticks();
//            assertTrue(match.getGame().isVerdictOut());
//            assertTrue(dealer.getStatus() == PlayerStatus.STICK);
//            assertTrue(player1.getStatus() == PlayerStatus.WON ||
//                    player1.getStatus() == PlayerStatus.LOST);
//            assertTrue(player3.getStatus() == PlayerStatus.WON ||
//                    player3.getStatus() == PlayerStatus.LOST);
//            if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
//                fail("WE SHOULD NOT BE HERE SINCE IF THE DEALER HAD ALSO BJ THE GAME SHOULD BE THROUGH ALREADY" +
//                        "BUT THIS MIGHT BE RELEVANT TO OTHER TESTS SO I LEAVE IT IN");
//                assertEquals(PlayerStatus.LOST, match.getGame().getPlayers().get(0).getStatus());
//            }
//            assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            wentThroughEveryScenario.put("All players stick, verdict is out", true);
//            sendChangedRound(match);
//            assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//            for (Player player : match.getGame().getPlayers()) {
//                assertEquals(PlayerStatus.BETTING, player.getStatus());
//            }
//            continue;
//        }
//

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    @Test
    void threePlayersOneDealer_Player1DropsOutAfterPlayer2PlayedAnotherPlayerGetsInAndPlays() {
        Helper3 helper3 = new Helper3();

        for (int i = 0; i < 100000; i++) {
//            System.out.println( i );
            user1 = new User("aa", "aa", "aa");
            user1.setMoney(100000);
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            user7 = new User("ww", "ww", "ww");
            user7.setMoney(100000);
            match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
            helper3.setMatch(match);
            helper3.addDealerToMatch(user7);
            helper3.addUserToMatch(user1, 2);
            helper3.addUserToMatch(user2, 3);

            helper3.startGame();
            Player player1 = match.getGame().getPlayers().get(0);
            Player player2 = match.getGame().getPlayers().get(1);
            Dealer dealer = match.getGame().getDealer();
            helper3.playerBet(bet1);
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();

            helper3.playerLeaves(player1);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);
            helper3.playerTurn();

            helper3.checkIfFinishedProperly();
            helper3.startGame();
            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
//            // if the playerWhoJustGotDealtBJ is not null, then our player had blackjack
//            // we then change the turn and turn playerWhoJustGotDealtBJ to null again.
//            // at this point the game can have taken lot's of different directions.
//            // the sendChangeTurn will keep running until the next player doesn't have BJ
//            // that means that 3-4 players can all have BJ, and the next turn goes to the 5 player
//            // we amend for that by having and if statement and checking the status of each player
//            // from now on. If they all had black jack, the round should be through and the players
//            // in a betting round
//            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                wentThroughEveryScenario.put("Player1 had BJ from first hand", true);
//                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                sendChangedTurn(match);
//                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            }
//
//            // if the bet has reset, that means the game is over (all had BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("All had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//            if (player1.getStatus() != PlayerStatus.BLACKJACK) { // the player didn't have BJ
//                wentThroughEveryScenario.put("Player1 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player1.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Player3 got BJ from first hand", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == player3);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                }
//            }
//
//            // if the bet has reset, that means the game is over (player3 and dealer BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("Player3 and dealer had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//
//            if (player3.getStatus() != PlayerStatus.BLACKJACK) { // the player3 didn't have BJ
//                wentThroughEveryScenario.put("Player3 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player3.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player3.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Dealer got BJ from first hand,  game was reset", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == dealer);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                    assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                    for (Player player : match.getGame().getPlayers()) {
//                        assertEquals(PlayerStatus.BETTING, player.getStatus());
//                    }
//                    continue;
//                }
//            }
//
//
//
//
//
//            // the next player, dealer in that case, should only be in a playing status, since if they had
//            // bj, the game would been through.
//            // they decide to stick. then, verdict should also be out, since
//            // the last player who his turn finished was the dealer. The dealer doesn't get
//            // neither LOST or WON status from the verdict, since he might won in one turn and lost in another
//            // so, the dealer has the same status as his last choice: STICK in that case.
//            // The player on the other hand, either have a won or a lost status.
//            assertTrue(dealer.getStatus() == PlayerStatus.PLAYING);
//            match.getGame().sticks();
//            assertTrue(match.getGame().isVerdictOut());
//            assertTrue(dealer.getStatus() == PlayerStatus.STICK);
//            assertTrue(player1.getStatus() == PlayerStatus.WON ||
//                    player1.getStatus() == PlayerStatus.LOST);
//            assertTrue(player3.getStatus() == PlayerStatus.WON ||
//                    player3.getStatus() == PlayerStatus.LOST);
//            if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
//                fail("WE SHOULD NOT BE HERE SINCE IF THE DEALER HAD ALSO BJ THE GAME SHOULD BE THROUGH ALREADY" +
//                        "BUT THIS MIGHT BE RELEVANT TO OTHER TESTS SO I LEAVE IT IN");
//                assertEquals(PlayerStatus.LOST, match.getGame().getPlayers().get(0).getStatus());
//            }
//            assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            wentThroughEveryScenario.put("All players stick, verdict is out", true);
//            sendChangedRound(match);
//            assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//            for (Player player : match.getGame().getPlayers()) {
//                assertEquals(PlayerStatus.BETTING, player.getStatus());
//            }
//            continue;
//        }
//

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    @Test
    void threePlayersOneDealer_Player1DropsOutAfterDealerPlayedAnotherPlayerGetsInAndPlays() {
        Helper3 helper3 = new Helper3();

        for (int i = 0; i < 100000; i++) {
//            System.out.println( i );
            user1 = new User("aa", "aa", "aa");
            user1.setMoney(100000);
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            user7 = new User("ww", "ww", "ww");
            user7.setMoney(100000);
            match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
            helper3.setMatch(match);
            helper3.addDealerToMatch(user7);
            helper3.addUserToMatch(user1, 2);
            helper3.addUserToMatch(user2, 3);

            helper3.startGame();
            Player player1 = match.getGame().getPlayers().get(0);
            Player player2 = match.getGame().getPlayers().get(1);
            Dealer dealer = match.getGame().getDealer();
            helper3.playerBet(bet1);
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();

            helper3.playerLeaves(player1);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);

            helper3.checkIfFinishedProperly();
            helper3.startGame();
            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
//            // if the playerWhoJustGotDealtBJ is not null, then our player had blackjack
//            // we then change the turn and turn playerWhoJustGotDealtBJ to null again.
//            // at this point the game can have taken lot's of different directions.
//            // the sendChangeTurn will keep running until the next player doesn't have BJ
//            // that means that 3-4 players can all have BJ, and the next turn goes to the 5 player
//            // we amend for that by having and if statement and checking the status of each player
//            // from now on. If they all had black jack, the round should be through and the players
//            // in a betting round
//            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                wentThroughEveryScenario.put("Player1 had BJ from first hand", true);
//                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                sendChangedTurn(match);
//                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            }
//
//            // if the bet has reset, that means the game is over (all had BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("All had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//            if (player1.getStatus() != PlayerStatus.BLACKJACK) { // the player didn't have BJ
//                wentThroughEveryScenario.put("Player1 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player1.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Player3 got BJ from first hand", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == player3);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                }
//            }
//
//            // if the bet has reset, that means the game is over (player3 and dealer BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("Player3 and dealer had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//
//            if (player3.getStatus() != PlayerStatus.BLACKJACK) { // the player3 didn't have BJ
//                wentThroughEveryScenario.put("Player3 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player3.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player3.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Dealer got BJ from first hand,  game was reset", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == dealer);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                    assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                    for (Player player : match.getGame().getPlayers()) {
//                        assertEquals(PlayerStatus.BETTING, player.getStatus());
//                    }
//                    continue;
//                }
//            }
//
//
//
//
//
//            // the next player, dealer in that case, should only be in a playing status, since if they had
//            // bj, the game would been through.
//            // they decide to stick. then, verdict should also be out, since
//            // the last player who his turn finished was the dealer. The dealer doesn't get
//            // neither LOST or WON status from the verdict, since he might won in one turn and lost in another
//            // so, the dealer has the same status as his last choice: STICK in that case.
//            // The player on the other hand, either have a won or a lost status.
//            assertTrue(dealer.getStatus() == PlayerStatus.PLAYING);
//            match.getGame().sticks();
//            assertTrue(match.getGame().isVerdictOut());
//            assertTrue(dealer.getStatus() == PlayerStatus.STICK);
//            assertTrue(player1.getStatus() == PlayerStatus.WON ||
//                    player1.getStatus() == PlayerStatus.LOST);
//            assertTrue(player3.getStatus() == PlayerStatus.WON ||
//                    player3.getStatus() == PlayerStatus.LOST);
//            if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
//                fail("WE SHOULD NOT BE HERE SINCE IF THE DEALER HAD ALSO BJ THE GAME SHOULD BE THROUGH ALREADY" +
//                        "BUT THIS MIGHT BE RELEVANT TO OTHER TESTS SO I LEAVE IT IN");
//                assertEquals(PlayerStatus.LOST, match.getGame().getPlayers().get(0).getStatus());
//            }
//            assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            wentThroughEveryScenario.put("All players stick, verdict is out", true);
//            sendChangedRound(match);
//            assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//            for (Player player : match.getGame().getPlayers()) {
//                assertEquals(PlayerStatus.BETTING, player.getStatus());
//            }
//            continue;
//        }
//

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    @Test
    void threePlayersButTheThirdJoinsAfterGameStart() {
        Helper3 helper3 = new Helper3();

        for (int i = 0; i < 100000; i++) {
//            System.out.println( i );
            user1 = new User("aa", "aa", "aa");
            user1.setMoney(100000);

            user7 = new User("ww", "ww", "ww");
            user7.setMoney(100000);
            match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
            helper3.setMatch(match);
            helper3.addDealerToMatch(user7);
            helper3.addUserToMatch(user1, 2);

            helper3.startGame();
            Player player1 = match.getGame().getPlayers().get(0);
            Dealer dealer = match.getGame().getDealer();

            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            helper3.addUserToMatch(user2, 3);

            helper3.playerBet(bet1);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();


            helper3.checkIfFinishedProperly();
            helper3.startGame();
            Player player2 = match.getGame().getPlayerByEmail(user2.getEmail());
            helper3.playerBet(bet1);
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
//            // if the playerWhoJustGotDealtBJ is not null, then our player had blackjack
//            // we then change the turn and turn playerWhoJustGotDealtBJ to null again.
//            // at this point the game can have taken lot's of different directions.
//            // the sendChangeTurn will keep running until the next player doesn't have BJ
//            // that means that 3-4 players can all have BJ, and the next turn goes to the 5 player
//            // we amend for that by having and if statement and checking the status of each player
//            // from now on. If they all had black jack, the round should be through and the players
//            // in a betting round
//            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                wentThroughEveryScenario.put("Player1 had BJ from first hand", true);
//                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                sendChangedTurn(match);
//                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            }
//
//            // if the bet has reset, that means the game is over (all had BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("All had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//            if (player1.getStatus() != PlayerStatus.BLACKJACK) { // the player didn't have BJ
//                wentThroughEveryScenario.put("Player1 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player1.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Player3 got BJ from first hand", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == player3);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                }
//            }
//
//            // if the bet has reset, that means the game is over (player3 and dealer BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("Player3 and dealer had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//
//            if (player3.getStatus() != PlayerStatus.BLACKJACK) { // the player3 didn't have BJ
//                wentThroughEveryScenario.put("Player3 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player3.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player3.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Dealer got BJ from first hand,  game was reset", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == dealer);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                    assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                    for (Player player : match.getGame().getPlayers()) {
//                        assertEquals(PlayerStatus.BETTING, player.getStatus());
//                    }
//                    continue;
//                }
//            }
//
//
//
//
//
//            // the next player, dealer in that case, should only be in a playing status, since if they had
//            // bj, the game would been through.
//            // they decide to stick. then, verdict should also be out, since
//            // the last player who his turn finished was the dealer. The dealer doesn't get
//            // neither LOST or WON status from the verdict, since he might won in one turn and lost in another
//            // so, the dealer has the same status as his last choice: STICK in that case.
//            // The player on the other hand, either have a won or a lost status.
//            assertTrue(dealer.getStatus() == PlayerStatus.PLAYING);
//            match.getGame().sticks();
//            assertTrue(match.getGame().isVerdictOut());
//            assertTrue(dealer.getStatus() == PlayerStatus.STICK);
//            assertTrue(player1.getStatus() == PlayerStatus.WON ||
//                    player1.getStatus() == PlayerStatus.LOST);
//            assertTrue(player3.getStatus() == PlayerStatus.WON ||
//                    player3.getStatus() == PlayerStatus.LOST);
//            if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
//                fail("WE SHOULD NOT BE HERE SINCE IF THE DEALER HAD ALSO BJ THE GAME SHOULD BE THROUGH ALREADY" +
//                        "BUT THIS MIGHT BE RELEVANT TO OTHER TESTS SO I LEAVE IT IN");
//                assertEquals(PlayerStatus.LOST, match.getGame().getPlayers().get(0).getStatus());
//            }
//            assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            wentThroughEveryScenario.put("All players stick, verdict is out", true);
//            sendChangedRound(match);
//            assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//            for (Player player : match.getGame().getPlayers()) {
//                assertEquals(PlayerStatus.BETTING, player.getStatus());
//            }
//            continue;
//        }
//

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    @Test
    void threePlayersButTheThirdJoinsAfterBetting() {
        Helper3 helper3 = new Helper3();

        for (int i = 0; i < 100000; i++) {
//            System.out.println( i );
            user1 = new User("aa", "aa", "aa");
            user1.setMoney(100000);

            user7 = new User("ww", "ww", "ww");
            user7.setMoney(100000);
            match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
            helper3.setMatch(match);
            helper3.addDealerToMatch(user7);
            helper3.addUserToMatch(user1, 2);

            helper3.startGame();
            Player player1 = match.getGame().getPlayers().get(0);
            Dealer dealer = match.getGame().getDealer();
            helper3.playerBet(bet1);

            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            helper3.addUserToMatch(user2, 3);

            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();


            helper3.checkIfFinishedProperly();
            helper3.startGame();
            Player player2 = match.getGame().getPlayerByEmail(user2.getEmail());
            helper3.playerBet(bet1);
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
//            // if the playerWhoJustGotDealtBJ is not null, then our player had blackjack
//            // we then change the turn and turn playerWhoJustGotDealtBJ to null again.
//            // at this point the game can have taken lot's of different directions.
//            // the sendChangeTurn will keep running until the next player doesn't have BJ
//            // that means that 3-4 players can all have BJ, and the next turn goes to the 5 player
//            // we amend for that by having and if statement and checking the status of each player
//            // from now on. If they all had black jack, the round should be through and the players
//            // in a betting round
//            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                wentThroughEveryScenario.put("Player1 had BJ from first hand", true);
//                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                sendChangedTurn(match);
//                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            }
//
//            // if the bet has reset, that means the game is over (all had BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("All had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//            if (player1.getStatus() != PlayerStatus.BLACKJACK) { // the player didn't have BJ
//                wentThroughEveryScenario.put("Player1 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player1.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Player3 got BJ from first hand", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == player3);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                }
//            }
//
//            // if the bet has reset, that means the game is over (player3 and dealer BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("Player3 and dealer had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//
//            if (player3.getStatus() != PlayerStatus.BLACKJACK) { // the player3 didn't have BJ
//                wentThroughEveryScenario.put("Player3 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player3.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player3.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Dealer got BJ from first hand,  game was reset", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == dealer);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                    assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                    for (Player player : match.getGame().getPlayers()) {
//                        assertEquals(PlayerStatus.BETTING, player.getStatus());
//                    }
//                    continue;
//                }
//            }
//
//
//
//
//
//            // the next player, dealer in that case, should only be in a playing status, since if they had
//            // bj, the game would been through.
//            // they decide to stick. then, verdict should also be out, since
//            // the last player who his turn finished was the dealer. The dealer doesn't get
//            // neither LOST or WON status from the verdict, since he might won in one turn and lost in another
//            // so, the dealer has the same status as his last choice: STICK in that case.
//            // The player on the other hand, either have a won or a lost status.
//            assertTrue(dealer.getStatus() == PlayerStatus.PLAYING);
//            match.getGame().sticks();
//            assertTrue(match.getGame().isVerdictOut());
//            assertTrue(dealer.getStatus() == PlayerStatus.STICK);
//            assertTrue(player1.getStatus() == PlayerStatus.WON ||
//                    player1.getStatus() == PlayerStatus.LOST);
//            assertTrue(player3.getStatus() == PlayerStatus.WON ||
//                    player3.getStatus() == PlayerStatus.LOST);
//            if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
//                fail("WE SHOULD NOT BE HERE SINCE IF THE DEALER HAD ALSO BJ THE GAME SHOULD BE THROUGH ALREADY" +
//                        "BUT THIS MIGHT BE RELEVANT TO OTHER TESTS SO I LEAVE IT IN");
//                assertEquals(PlayerStatus.LOST, match.getGame().getPlayers().get(0).getStatus());
//            }
//            assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            wentThroughEveryScenario.put("All players stick, verdict is out", true);
//            sendChangedRound(match);
//            assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//            for (Player player : match.getGame().getPlayers()) {
//                assertEquals(PlayerStatus.BETTING, player.getStatus());
//            }
//            continue;
//        }
//

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    @Test
    void threePlayersButTheThirdJoinsAfterPlayerPlayed() {
        Helper3 helper3 = new Helper3();

        for (int i = 0; i < 100000; i++) {
//            System.out.println( i );
            user1 = new User("aa", "aa", "aa");
            user1.setMoney(100000);

            user7 = new User("ww", "ww", "ww");
            user7.setMoney(100000);
            match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
            helper3.setMatch(match);
            helper3.addDealerToMatch(user7);
            helper3.addUserToMatch(user1, 2);

            helper3.startGame();
            Player player1 = match.getGame().getPlayers().get(0);
            Dealer dealer = match.getGame().getDealer();
            helper3.playerBet(bet1);


            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            helper3.addUserToMatch(user2, 3);
            helper3.playerTurn();


            helper3.checkIfFinishedProperly();
            helper3.startGame();
            Player player2 = match.getGame().getPlayerByEmail(user2.getEmail());
            helper3.playerBet(bet1);
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
//            // if the playerWhoJustGotDealtBJ is not null, then our player had blackjack
//            // we then change the turn and turn playerWhoJustGotDealtBJ to null again.
//            // at this point the game can have taken lot's of different directions.
//            // the sendChangeTurn will keep running until the next player doesn't have BJ
//            // that means that 3-4 players can all have BJ, and the next turn goes to the 5 player
//            // we amend for that by having and if statement and checking the status of each player
//            // from now on. If they all had black jack, the round should be through and the players
//            // in a betting round
//            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                wentThroughEveryScenario.put("Player1 had BJ from first hand", true);
//                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                sendChangedTurn(match);
//                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            }
//
//            // if the bet has reset, that means the game is over (all had BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("All had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//            if (player1.getStatus() != PlayerStatus.BLACKJACK) { // the player didn't have BJ
//                wentThroughEveryScenario.put("Player1 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player1.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Player3 got BJ from first hand", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == player3);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                }
//            }
//
//            // if the bet has reset, that means the game is over (player3 and dealer BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("Player3 and dealer had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//
//            if (player3.getStatus() != PlayerStatus.BLACKJACK) { // the player3 didn't have BJ
//                wentThroughEveryScenario.put("Player3 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player3.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player3.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Dealer got BJ from first hand,  game was reset", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == dealer);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                    assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                    for (Player player : match.getGame().getPlayers()) {
//                        assertEquals(PlayerStatus.BETTING, player.getStatus());
//                    }
//                    continue;
//                }
//            }
//
//
//
//
//
//            // the next player, dealer in that case, should only be in a playing status, since if they had
//            // bj, the game would been through.
//            // they decide to stick. then, verdict should also be out, since
//            // the last player who his turn finished was the dealer. The dealer doesn't get
//            // neither LOST or WON status from the verdict, since he might won in one turn and lost in another
//            // so, the dealer has the same status as his last choice: STICK in that case.
//            // The player on the other hand, either have a won or a lost status.
//            assertTrue(dealer.getStatus() == PlayerStatus.PLAYING);
//            match.getGame().sticks();
//            assertTrue(match.getGame().isVerdictOut());
//            assertTrue(dealer.getStatus() == PlayerStatus.STICK);
//            assertTrue(player1.getStatus() == PlayerStatus.WON ||
//                    player1.getStatus() == PlayerStatus.LOST);
//            assertTrue(player3.getStatus() == PlayerStatus.WON ||
//                    player3.getStatus() == PlayerStatus.LOST);
//            if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
//                fail("WE SHOULD NOT BE HERE SINCE IF THE DEALER HAD ALSO BJ THE GAME SHOULD BE THROUGH ALREADY" +
//                        "BUT THIS MIGHT BE RELEVANT TO OTHER TESTS SO I LEAVE IT IN");
//                assertEquals(PlayerStatus.LOST, match.getGame().getPlayers().get(0).getStatus());
//            }
//            assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            wentThroughEveryScenario.put("All players stick, verdict is out", true);
//            sendChangedRound(match);
//            assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//            for (Player player : match.getGame().getPlayers()) {
//                assertEquals(PlayerStatus.BETTING, player.getStatus());
//            }
//            continue;
//        }
//

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    @Test
    void threePlayersButTheThirdJoinsAfterDealerPlayed() {
        Helper3 helper3 = new Helper3();

        for (int i = 0; i < 100000; i++) {
//            System.out.println( i );
            user1 = new User("aa", "aa", "aa");
            user1.setMoney(100000);

            user7 = new User("ww", "ww", "ww");
            user7.setMoney(100000);
            match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
            helper3.setMatch(match);
            helper3.addDealerToMatch(user7);
            helper3.addUserToMatch(user1, 2);

            helper3.startGame();
            Player player1 = match.getGame().getPlayers().get(0);
            Dealer dealer = match.getGame().getDealer();
            helper3.playerBet(bet1);


            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();

            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            helper3.addUserToMatch(user2, 3);

            helper3.checkIfFinishedProperly();
            helper3.startGame();
            Player player2 = match.getGame().getPlayerByEmail(user2.getEmail());
            helper3.playerBet(bet1);
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
//            // if the playerWhoJustGotDealtBJ is not null, then our player had blackjack
//            // we then change the turn and turn playerWhoJustGotDealtBJ to null again.
//            // at this point the game can have taken lot's of different directions.
//            // the sendChangeTurn will keep running until the next player doesn't have BJ
//            // that means that 3-4 players can all have BJ, and the next turn goes to the 5 player
//            // we amend for that by having and if statement and checking the status of each player
//            // from now on. If they all had black jack, the round should be through and the players
//            // in a betting round
//            if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                wentThroughEveryScenario.put("Player1 had BJ from first hand", true);
//                assertEquals(player1, match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                sendChangedTurn(match);
//                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            }
//
//            // if the bet has reset, that means the game is over (all had BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("All had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, match.getGame().getDealer().getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//            if (player1.getStatus() != PlayerStatus.BLACKJACK) { // the player didn't have BJ
//                wentThroughEveryScenario.put("Player1 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player1.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player1.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Player3 got BJ from first hand", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == player3);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                }
//            }
//
//            // if the bet has reset, that means the game is over (player3 and dealer BJ)
//            if (player1.getBet() == 0) {
//                wentThroughEveryScenario.put("Player3 and dealer had BJ, game was reset", true);
//                assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                for (Player player : match.getGame().getPlayers()) {
//                    assertEquals(PlayerStatus.BETTING, player.getStatus());
//                }
//                continue;
//            }
//
//
//            if (player3.getStatus() != PlayerStatus.BLACKJACK) { // the player3 didn't have BJ
//                wentThroughEveryScenario.put("Player3 played, didn't have BJ from first hand", true);
//                assertEquals(PlayerStatus.PLAYING, player3.getStatus());
//                match.getGame().sticks();
//                assertEquals(PlayerStatus.STICK, player3.getStatus());
//                if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
//                    wentThroughEveryScenario.put("Dealer got BJ from first hand,  game was reset", true);
//                    assertTrue(match.getGame().getPlayerWhoJustGotDealtBlackJack() == dealer);
//                    sendChangedTurn(match);
//                    assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//                    assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//                    for (Player player : match.getGame().getPlayers()) {
//                        assertEquals(PlayerStatus.BETTING, player.getStatus());
//                    }
//                    continue;
//                }
//            }
//
//
//
//
//
//            // the next player, dealer in that case, should only be in a playing status, since if they had
//            // bj, the game would been through.
//            // they decide to stick. then, verdict should also be out, since
//            // the last player who his turn finished was the dealer. The dealer doesn't get
//            // neither LOST or WON status from the verdict, since he might won in one turn and lost in another
//            // so, the dealer has the same status as his last choice: STICK in that case.
//            // The player on the other hand, either have a won or a lost status.
//            assertTrue(dealer.getStatus() == PlayerStatus.PLAYING);
//            match.getGame().sticks();
//            assertTrue(match.getGame().isVerdictOut());
//            assertTrue(dealer.getStatus() == PlayerStatus.STICK);
//            assertTrue(player1.getStatus() == PlayerStatus.WON ||
//                    player1.getStatus() == PlayerStatus.LOST);
//            assertTrue(player3.getStatus() == PlayerStatus.WON ||
//                    player3.getStatus() == PlayerStatus.LOST);
//            if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
//                fail("WE SHOULD NOT BE HERE SINCE IF THE DEALER HAD ALSO BJ THE GAME SHOULD BE THROUGH ALREADY" +
//                        "BUT THIS MIGHT BE RELEVANT TO OTHER TESTS SO I LEAVE IT IN");
//                assertEquals(PlayerStatus.LOST, match.getGame().getPlayers().get(0).getStatus());
//            }
//            assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
//            wentThroughEveryScenario.put("All players stick, verdict is out", true);
//            sendChangedRound(match);
//            assertEquals(PlayerStatus.WAITING_TURN, dealer.getStatus());
//            for (Player player : match.getGame().getPlayers()) {
//                assertEquals(PlayerStatus.BETTING, player.getStatus());
//            }
//            continue;
//        }
//

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }
}