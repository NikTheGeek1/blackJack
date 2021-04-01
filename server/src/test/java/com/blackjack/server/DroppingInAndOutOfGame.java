package com.blackjack.server;

import com.blackjack.server.models.User;
import com.blackjack.server.models.game.*;
import com.blackjack.server.models.match.GamePrivacy;
import com.blackjack.server.models.match.GameType;
import com.blackjack.server.models.match.Match;
import com.blackjack.server.utils.Player.TokenUtils;
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
    HashMap<String, HashMap<String, Integer>> playerMoneyBeforeSomeoneLeave = new HashMap<>();
    HashMap<String, HashMap<String, Integer>> playerBetBeforeSomeoneLeave = new HashMap<>();


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
            assertEquals(0, TokenUtils.tokensToMoney(player.getBetTokens()));
            assertEquals(PlayerStatus.BETTING, player.getStatus());
        }
        assertTrue(match.getGame().getDealer().getIsDealer());
        assertEquals(2, match.getGame().getDealer().getCards().size());
        assertEquals(1, match.getGame().getDealer().getRevealedCards().size());
        assertEquals(0, TokenUtils.tokensToMoney(match.getGame().getDealer().getBetTokens()));
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
        match.getGame().placeBet(bet.getPlayerEmail(), bet.getBetTokens());
        assertEquals(PlayerStatus.WAITING_TURN, player.getStatus());
        assertEquals(bet.getBetTokens(), player.getBetTokens());
        assertEquals(1, player.getRevealedCards().size());
    }

    public void assertIfPlayerIsInStartingPosition(Player player) {
        assertEquals(PlayerStatus.WAITING_GAME, player.getStatus());
        if (player == match.getGame().getDealer()) {
            assertTrue(player.getIsDealer());
        } else {
            assertFalse(player.getIsDealer());
        }
        assertEquals(0, player.getCards().size());
        assertEquals(0, player.getRevealedCards().size());
        assertEquals(0, TokenUtils.tokensToMoney(player.getBetTokens()));
    }
    public void assertIfGameResetProperly (String type) {
        switch (type) {
            case "PLAYER_LEFT": {
                for (Player player : match.getGame().getPlayers()) {
                    assertNotEquals(PlayerStatus.WAITING_GAME, player.getStatus());
                    assertEquals(2, player.getCards().size());
                }
                assertNotEquals(PlayerStatus.WAITING_GAME, match.getGame().getDealer().getStatus());
                assertEquals(2, match.getGame().getDealer().getCards().size());
                break;
            }

            case "DEALER_LEFT_OR_PLAYER_LEFT_ALONE": {
                for (Player player : match.getGame().getPlayers()) {
                    assertIfPlayerIsInStartingPosition(player);
                }
                assertNotNull(match.getGame().getDealer());
                assertIfPlayerIsInStartingPosition(match.getGame().getDealer());
                assertNull(match.getGame().getPlayerWhoJustGotDealtBlackJack());
                break;
            }
            default: {
                fail("NO CASE SELECTED");
            }
        }

    }

    public void assertIfPlayerArrivedProperly(String type, Player newComer) {
        switch (type) {
            case "AFTER_DEALER_LEFT_OR_PLAYER_LEFT_ALONE": {
                assertIfPlayerIsInStartingPosition(newComer);
                break;
            }
            case "WHEN_GAME_IS_ALREADY_ON": {
                assertEquals(PlayerStatus.WAITING_GAME, newComer.getStatus());
                assertFalse(newComer.getIsDealer());
                assertEquals(0, newComer.getCards().size());
                assertEquals(0, newComer.getRevealedCards().size());
                assertEquals(0, TokenUtils.tokensToMoney(newComer.getBetTokens()));
                break;
            }
            default:
                fail("WRONG TYPE");
        }
    }

    public void setPlayerMoneyAndBetBeforeSomeoneLeave() {
        match.getGame().getPlayers().forEach(player -> playerMoneyBeforeSomeoneLeave.put(player.getEmail(), new HashMap<>(player.getTokens())));
        playerMoneyBeforeSomeoneLeave.put(match.getGame().getDealer().getEmail(), new HashMap<>(match.getGame().getDealer().getTokens()));
        match.getGame().getPlayers().forEach(player -> playerBetBeforeSomeoneLeave.put(player.getEmail(), new HashMap<>(player.getBetTokens())));
        playerBetBeforeSomeoneLeave.put(match.getGame().getDealer().getEmail(), new HashMap<>(match.getGame().getDealer().getBetTokens()));
    }
    public double getTotalSumOfBets() {
        double total = 0;
        for(String playerEmail : playerBetBeforeSomeoneLeave.keySet()) {
            total += TokenUtils.tokensToMoney(playerBetBeforeSomeoneLeave.get(playerEmail));
        }
        return total;
    }
    public void assertIfDebts (String type, User leaver) {
        switch (type) {
            case "DEALER_LEFT_ILLEGAL":{
                match.getGame().getPlayers().forEach(player -> {
                    assertEquals(
                            TokenUtils.tokensToMoney(playerMoneyBeforeSomeoneLeave.get(player.getEmail())) + (TokenUtils.tokensToMoney(playerBetBeforeSomeoneLeave.get(player.getEmail())) * 2),
                            TokenUtils.tokensToMoney(player.getTokens()));
                });
                assertEquals(
                        TokenUtils.tokensToMoney(playerMoneyBeforeSomeoneLeave.get(leaver.getEmail())) - getTotalSumOfBets(),
                        leaver.getMoney());
                break;
            }
            case "PLAYER_LEFT_ILLEGAL": {
                assertEquals(
                        TokenUtils.tokensToMoney(playerMoneyBeforeSomeoneLeave.get(dealer.getEmail())) + TokenUtils.tokensToMoney(playerBetBeforeSomeoneLeave.get(leaver.getEmail())),
                        TokenUtils.tokensToMoney(dealer.getTokens()));
                assertEquals(
                        TokenUtils.tokensToMoney(playerMoneyBeforeSomeoneLeave.get(leaver.getEmail())),
                        leaver.getMoney());
                break;
            }
            case "PLAYER_LEFT_LEGAL": {
                match.getGame().getPlayers().forEach(player -> {
                    assertEquals(
                            TokenUtils.tokensToMoney(playerMoneyBeforeSomeoneLeave.get(player.getEmail())),
                            TokenUtils.tokensToMoney(player.getTokens()));
                });
                assertEquals(
                        TokenUtils.tokensToMoney(playerMoneyBeforeSomeoneLeave.get(match.getGame().getDealer().getEmail())),
                        TokenUtils.tokensToMoney(match.getGame().getDealer().getTokens()));
                break;
            }
            default:
                fail("WRONG TYPE");
        }
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

            match.getGame().nextTurn(match);
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
        bet1 = new Bet("aa", TokenUtils.moneyToTokens(1));
        bet2 = new Bet("bb", TokenUtils.moneyToTokens(1));
        bet3 = new Bet("cc", TokenUtils.moneyToTokens(1));
        bet4 = new Bet("dd", TokenUtils.moneyToTokens(1));
        bet5 = new Bet("ee", TokenUtils.moneyToTokens(1));
        bet6 = new Bet("ff", TokenUtils.moneyToTokens(1));
        bet7 = new Bet("ww", TokenUtils.moneyToTokens(1));

        match = new Match("aa", 7, GameType.HUMANS, GamePrivacy.PUBLIC);
    }

    private void sendChangedRound(Match match) {
        match.getGame().nextRound(match.getGameType());
    }
    private void sendChangedTurn(Match match) {
        match.getGame().nextTurn(match);
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
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();




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
            match.getGame().startRound(match);

            helper3.setPlayerMoneyAndBetBeforeSomeoneLeave();
            helper3.playerLeaves(player1);
            helper3.assertIfDebts("PLAYER_LEFT_ILLEGAL", user1);
            helper3.assertIfGameResetProperly("DEALER_LEFT_OR_PLAYER_LEFT_ALONE");
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            helper3.addUserToMatch(user2, 2);
            helper3.assertIfPlayerArrivedProperly("AFTER_DEALER_LEFT_OR_PLAYER_LEFT_ALONE", match.getGame().getPlayerByEmail(user2.getEmail()));

            helper3.startGame();
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
            Player player2 = match.getGame().getPlayerByEmail(user2.getEmail());
            helper3.playerBet(bet2);

            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();


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

            helper3.setPlayerMoneyAndBetBeforeSomeoneLeave();
            helper3.playerLeaves(player1);
            helper3.assertIfDebts("PLAYER_LEFT_LEGAL", user1);
            helper3.assertIfGameResetProperly("DEALER_LEFT_OR_PLAYER_LEFT_ALONE");
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            helper3.addUserToMatch(user2, 2);
            helper3.assertIfPlayerArrivedProperly("AFTER_DEALER_LEFT_OR_PLAYER_LEFT_ALONE", match.getGame().getPlayerByEmail(user2.getEmail()));

            helper3.startGame();
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
            Player player2 = match.getGame().getPlayerByEmail(user2.getEmail());
            helper3.playerBet(bet2);

            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();


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

            helper3.setPlayerMoneyAndBetBeforeSomeoneLeave();
            helper3.playerLeaves(match.getGame().getPlayers().get(0));
            helper3.assertIfDebts("PLAYER_LEFT_LEGAL", user7);
            helper3.assertIfGameResetProperly("DEALER_LEFT_OR_PLAYER_LEFT_ALONE");
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            helper3.addUserToMatch(user2, 2);
            helper3.assertIfPlayerArrivedProperly("AFTER_DEALER_LEFT_OR_PLAYER_LEFT_ALONE", match.getGame().getPlayerByEmail(user2.getEmail()));



            helper3.startGame();
            Dealer dealer = match.getGame().getDealer();
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
            Player player2 = match.getGame().getPlayerByEmail(user2.getEmail());
            helper3.playerBet(bet2);

            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
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
            match.getGame().startRound(match);

            helper3.setPlayerMoneyAndBetBeforeSomeoneLeave();
            helper3.playerLeaves(dealer);
            helper3.assertIfDebts("DEALER_LEFT_ILLEGAL", user7);
            helper3.assertIfGameResetProperly("DEALER_LEFT_OR_PLAYER_LEFT_ALONE");
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            helper3.addUserToMatch(user2, 2);
            helper3.assertIfPlayerArrivedProperly("AFTER_DEALER_LEFT_OR_PLAYER_LEFT_ALONE", match.getGame().getPlayerByEmail(user2.getEmail()));


            helper3.startGame();
            Player newPlayer1 = match.getGame().getPlayers().get(0);
            Dealer dealer2 = match.getGame().getDealer();
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();



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

            helper3.setPlayerMoneyAndBetBeforeSomeoneLeave();
            helper3.playerLeaves(dealer);
            helper3.assertIfDebts("DEALER_LEFT_ILLEGAL", user7);
            helper3.assertIfGameResetProperly("DEALER_LEFT_OR_PLAYER_LEFT_ALONE");
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            helper3.addUserToMatch(user2, 2);
            helper3.assertIfPlayerArrivedProperly("AFTER_DEALER_LEFT_OR_PLAYER_LEFT_ALONE", match.getGame().getPlayerByEmail(user2.getEmail()));


            helper3.startGame();
            Player newPlayer1 = match.getGame().getPlayers().get(0);
            Dealer dealer2 = match.getGame().getDealer();
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();


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

            helper3.setPlayerMoneyAndBetBeforeSomeoneLeave();
            helper3.playerLeaves(match.getGame().getDealer());
            helper3.assertIfDebts("PLAYER_LEFT_LEGAL", user7);
            helper3.assertIfGameResetProperly("DEALER_LEFT_OR_PLAYER_LEFT_ALONE");
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            helper3.addUserToMatch(user2, 2);
            helper3.assertIfPlayerArrivedProperly("AFTER_DEALER_LEFT_OR_PLAYER_LEFT_ALONE", match.getGame().getPlayerByEmail(user2.getEmail()));


            helper3.startGame();
            Player newPlayer1 = match.getGame().getPlayers().get(0);
            Dealer dealer2 = match.getGame().getDealer();
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();


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
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();



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
            match.getGame().startRound(match);

            helper3.setPlayerMoneyAndBetBeforeSomeoneLeave();
            helper3.playerLeaves(player1);
            helper3.assertIfDebts("PLAYER_LEFT_ILLEGAL", user1);
            helper3.assertIfGameResetProperly("PLAYER_LEFT");
            helper3.assertIfGameResetProperly("PLAYER_LEFT");
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);
            helper3.assertIfPlayerArrivedProperly("WHEN_GAME_IS_ALREADY_ON", match.getGame().getPlayerByEmail(user3.getEmail()));

//            helper3.playerTurn("PLAYER1");
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

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

            helper3.setPlayerMoneyAndBetBeforeSomeoneLeave();
            helper3.playerLeaves(player1);
            helper3.assertIfDebts("PLAYER_LEFT_LEGAL", user1);
            helper3.assertIfGameResetProperly("PLAYER_LEFT");
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);
            helper3.assertIfPlayerArrivedProperly("WHEN_GAME_IS_ALREADY_ON", match.getGame().getPlayerByEmail(user3.getEmail()));

            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
//            helper3.playerTurn("PLAYER1");
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
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

            helper3.setPlayerMoneyAndBetBeforeSomeoneLeave();
            helper3.playerLeaves(player1);
            helper3.assertIfDebts("PLAYER_LEFT_ILLEGAL", user1);
            helper3.assertIfGameResetProperly("PLAYER_LEFT");
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);
            helper3.assertIfPlayerArrivedProperly("WHEN_GAME_IS_ALREADY_ON", match.getGame().getPlayerByEmail(user3.getEmail()));

            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
//            helper3.playerTurn("PLAYER1");
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
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

            helper3.setPlayerMoneyAndBetBeforeSomeoneLeave();
            helper3.playerLeaves(player1);
            helper3.assertIfDebts("PLAYER_LEFT_ILLEGAL", user1);
            helper3.assertIfGameResetProperly("PLAYER_LEFT");
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);
            helper3.assertIfPlayerArrivedProperly("WHEN_GAME_IS_ALREADY_ON", match.getGame().getPlayerByEmail(user3.getEmail()));

            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);

//            helper3.playerTurn("PLAYER1");
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
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
            match.getGame().startRound(match);
            helper3.playerTurn();


            helper3.setPlayerMoneyAndBetBeforeSomeoneLeave();
            helper3.playerLeaves(player1);
            helper3.assertIfDebts("PLAYER_LEFT_ILLEGAL", user1);
            helper3.assertIfGameResetProperly("PLAYER_LEFT");
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);
            helper3.assertIfPlayerArrivedProperly("WHEN_GAME_IS_ALREADY_ON", match.getGame().getPlayerByEmail(user3.getEmail()));

            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

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
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();

            helper3.setPlayerMoneyAndBetBeforeSomeoneLeave();
            helper3.playerLeaves(player1);
            helper3.assertIfDebts("PLAYER_LEFT_ILLEGAL", user1);
            helper3.assertIfGameResetProperly("PLAYER_LEFT");
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);
            helper3.assertIfPlayerArrivedProperly("WHEN_GAME_IS_ALREADY_ON", match.getGame().getPlayerByEmail(user3.getEmail()));


            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

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
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();

            helper3.setPlayerMoneyAndBetBeforeSomeoneLeave();
            helper3.playerLeaves(player1);
            helper3.assertIfDebts("PLAYER_LEFT_LEGAL", user1);
            helper3.assertIfGameResetProperly("PLAYER_LEFT");
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
            helper3.removeCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);
            helper3.assertIfPlayerArrivedProperly("WHEN_GAME_IS_ALREADY_ON", match.getGame().getPlayerByEmail(user3.getEmail()));

            helper3.checkIfFinishedProperly();
            helper3.startGame();
            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
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
            helper3.assertIfPlayerArrivedProperly("WHEN_GAME_IS_ALREADY_ON", match.getGame().getPlayerByEmail(user2.getEmail()));

            helper3.playerBet(bet1);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();


            helper3.checkIfFinishedProperly();
            helper3.startGame();
            Player player2 = match.getGame().getPlayerByEmail(user2.getEmail());
            helper3.playerBet(bet1);
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

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
            helper3.assertIfPlayerArrivedProperly("WHEN_GAME_IS_ALREADY_ON", match.getGame().getPlayerByEmail(user2.getEmail()));

            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();


            helper3.checkIfFinishedProperly();
            helper3.startGame();
            Player player2 = match.getGame().getPlayerByEmail(user2.getEmail());
            helper3.playerBet(bet1);
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

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
            match.getGame().startRound(match);
            helper3.playerTurn();
            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            helper3.addUserToMatch(user2, 3);
            helper3.assertIfPlayerArrivedProperly("WHEN_GAME_IS_ALREADY_ON", match.getGame().getPlayerByEmail(user2.getEmail()));

            helper3.playerTurn();


            helper3.checkIfFinishedProperly();
            helper3.startGame();
            Player player2 = match.getGame().getPlayerByEmail(user2.getEmail());
            helper3.playerBet(bet1);
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

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
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();

            user2 = new User("bb", "bb", "bb");
            user2.setMoney(100000);
            helper3.addUserToMatch(user2, 3);
            helper3.assertIfPlayerArrivedProperly("WHEN_GAME_IS_ALREADY_ON", match.getGame().getPlayerByEmail(user2.getEmail()));

            helper3.checkIfFinishedProperly();
            helper3.startGame();
            Player player2 = match.getGame().getPlayerByEmail(user2.getEmail());
            helper3.playerBet(bet1);
            helper3.playerBet(bet2);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    @Test
    void threePlayersOneDealer_DealerDropsOutAfterBettingAnotherPlayerGetsInAndPlays() {
        Helper3 helper3 = new Helper3();

        for (int i = 0; i < 100000; i++) {
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
            match.getGame().startRound(match);

            helper3.setPlayerMoneyAndBetBeforeSomeoneLeave();
            helper3.playerLeaves(dealer);
            helper3.assertIfDebts("DEALER_LEFT_ILLEGAL", user7);
            helper3.assertIfGameResetProperly("DEALER_LEFT_OR_PLAYER_LEFT_ALONE");

            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);
            helper3.assertIfPlayerArrivedProperly("AFTER_DEALER_LEFT_OR_PLAYER_LEFT_ALONE", match.getGame().getPlayerByEmail(user3.getEmail()));

            helper3.startGame();

            Dealer dealer2 = match.getGame().getDealer();
            Player newPlayer1 = match.getGame().getPlayers().get(0);
            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    @Test
    void threePlayersOneDealer_DealerDropsOutBeforeBettingAnotherPlayerGetsInAndPlays() {
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

            helper3.setPlayerMoneyAndBetBeforeSomeoneLeave();
            helper3.playerLeaves(dealer);
            helper3.assertIfDebts("PLAYER_LEFT_LEGAL", user7);
            helper3.assertIfGameResetProperly("DEALER_LEFT_OR_PLAYER_LEFT_ALONE");
            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);
            helper3.assertIfPlayerArrivedProperly("AFTER_DEALER_LEFT_OR_PLAYER_LEFT_ALONE", match.getGame().getPlayerByEmail(user3.getEmail()));

            helper3.startGame();

            Dealer dealer2 = match.getGame().getDealer();
            Player newPlayer1 = match.getGame().getPlayers().get(0);
            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    @Test
    void threePlayersOneDealer_DealerDropsOutJUSTBeforeBettingAnotherPlayerGetsInAndPlays() {
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

            helper3.setPlayerMoneyAndBetBeforeSomeoneLeave();
            helper3.playerLeaves(dealer);
            helper3.assertIfDebts("DEALER_LEFT_ILLEGAL", user7);
            helper3.assertIfGameResetProperly("DEALER_LEFT_OR_PLAYER_LEFT_ALONE");
            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);
            helper3.assertIfPlayerArrivedProperly("AFTER_DEALER_LEFT_OR_PLAYER_LEFT_ALONE", match.getGame().getPlayerByEmail(user3.getEmail()));


            helper3.startGame();

            Dealer dealer2 = match.getGame().getDealer();
            Player newPlayer1 = match.getGame().getPlayers().get(0);
            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();
        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    @Test
    void threePlayersOneDealer_DealerDropsOutAfterPlayer1PlaysAnotherPlayerGetsInAndPlays() {
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
            match.getGame().startRound(match);
            helper3.playerTurn();

            helper3.setPlayerMoneyAndBetBeforeSomeoneLeave();
            helper3.playerLeaves(dealer);
            helper3.assertIfDebts("DEALER_LEFT_ILLEGAL", user7);
            helper3.assertIfGameResetProperly("DEALER_LEFT_OR_PLAYER_LEFT_ALONE");
            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);
            helper3.assertIfPlayerArrivedProperly("AFTER_DEALER_LEFT_OR_PLAYER_LEFT_ALONE", match.getGame().getPlayerByEmail(user3.getEmail()));


            helper3.startGame();

            Dealer dealer2 = match.getGame().getDealer();
            Player newPlayer1 = match.getGame().getPlayers().get(0);
            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    @Test
    void threePlayersOneDealer_DealerDropsOutAfterPlayer2PlaysAnotherPlayerGetsInAndPlays() {
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
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();


            helper3.setPlayerMoneyAndBetBeforeSomeoneLeave();
            helper3.playerLeaves(dealer);
            helper3.assertIfDebts("DEALER_LEFT_ILLEGAL", user7);
            helper3.assertIfGameResetProperly("DEALER_LEFT_OR_PLAYER_LEFT_ALONE");
            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);
            helper3.assertIfPlayerArrivedProperly("AFTER_DEALER_LEFT_OR_PLAYER_LEFT_ALONE", match.getGame().getPlayerByEmail(user3.getEmail()));


            helper3.startGame();

            Dealer dealer2 = match.getGame().getDealer();
            Player newPlayer1 = match.getGame().getPlayers().get(0);
            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();


        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }

    @Test
    void threePlayersOneDealer_DealerDropsOutAfterPlaysAnotherPlayerGetsInAndPlays() {
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
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();



            helper3.setPlayerMoneyAndBetBeforeSomeoneLeave();
            helper3.playerLeaves(dealer);
            helper3.assertIfDebts("PLAYER_LEFT_LEGAL", user7);
            helper3.assertIfGameResetProperly("DEALER_LEFT_OR_PLAYER_LEFT_ALONE");
            user3 = new User("cc", "cc", "cc");
            user3.setMoney(100000);
            helper3.addUserToMatch(user3, 3);
            helper3.assertIfPlayerArrivedProperly("AFTER_DEALER_LEFT_OR_PLAYER_LEFT_ALONE", match.getGame().getPlayerByEmail(user3.getEmail()));


            helper3.startGame();
            Dealer dealer2 = match.getGame().getDealer();
            Player newPlayer1 = match.getGame().getPlayers().get(0);
            Player player3 = match.getGame().getPlayerByEmail(user3.getEmail());
            helper3.playerBet(bet2);
            helper3.playerBet(bet3);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.playerTurn();
            helper3.checkIfFinishedProperly();
            helper3.startGame();

        }
        helper3.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });

    }


}