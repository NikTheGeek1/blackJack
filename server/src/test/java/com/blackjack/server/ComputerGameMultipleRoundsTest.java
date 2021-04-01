package com.blackjack.server;

import com.blackjack.server.Game_Computer.CustomAssertions;
import com.blackjack.server.Game_Computer.Helper;
import com.blackjack.server.models.User;
import com.blackjack.server.models.game.Bet;
import com.blackjack.server.models.match.GamePrivacy;
import com.blackjack.server.models.match.GameType;
import com.blackjack.server.models.match.Match;
import com.blackjack.server.utils.Player.TokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComputerGameMultipleRoundsTest {

    User user1;
    User user2;
    User user3;
    User user4;
    User user5;
    User user6;
    Bet bet1;
    Bet bet2;
    Bet bet3;
    Bet bet4;
    Bet bet5;
    Bet bet6;
    Match match;


    @BeforeEach
    void setUp() {
        user1 = new User("aa", "aa", "aa");
        user1.setMoney(100000);
        user2 = new User("bb", "bb", "bb");
        user2.setMoney(100000);
        user3 = new User("cc", "cc", "cc");
        user3.setMoney(100000);
        user4 = new User("dd", "dd", "dd");
        user4.setMoney(100000);
        user5 = new User("ee", "ee", "ee");
        user5.setMoney(100000);
        user6 = new User("ff", "ff", "ff");
        user6.setMoney(100000);

        bet1 = new Bet("aa", TokenUtils.moneyToTokens(1));
        bet2 = new Bet("bb", TokenUtils.moneyToTokens(1));
        bet3 = new Bet("cc", TokenUtils.moneyToTokens(1));
        bet4 = new Bet("dd", TokenUtils.moneyToTokens(1));
        bet5 = new Bet("ee", TokenUtils.moneyToTokens(1));
        bet6 = new Bet("ff", TokenUtils.moneyToTokens(1));

    }


    @Test
    public void shouldBeAbleToPlayJustAPlayer() {
        Helper helper = new Helper();
        helper.addCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.DEALER_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.DEALER_HAD_BJ_FROM_FIRST_HAND);
        match = new Match("aa", 7, GameType.COMPUTER, GamePrivacy.PUBLIC);
        helper.setMatch(match);
        helper.addUserToMatch(user1, 2);
        helper.startGame();

        for (int i = 0; i<10000; i++) {
            helper.playerBet(bet1);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper.playerTurn();
            if (match.getGame().isVerdictOut()) {
                helper.checkIfFinishedProperly();
            } else {
                helper.playerTurn();
                helper.checkIfFinishedProperly();
            }
            match.getGame().nextRound(match.getGameType());
        }
        helper.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });
    }

    @Test
    public void shouldBeAbleToPlayWithSixPlayers() {
        Helper helper = new Helper();
        helper.addCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER2_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER2_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER3_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER3_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER4_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER4_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER5_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER5_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER6_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER6_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.DEALER_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.DEALER_HAD_BJ_FROM_FIRST_HAND);
        match = new Match("aa", 7, GameType.COMPUTER, GamePrivacy.PUBLIC);
        helper.setMatch(match);
        helper.addUserToMatch(user1, 2);
        helper.addUserToMatch(user2, 3);
        helper.addUserToMatch(user3, 4);
        helper.addUserToMatch(user4, 5);
        helper.addUserToMatch(user5, 6);
        helper.addUserToMatch(user6, 7);
        helper.startGame();

        for (int i = 0; i<10000; i++) {
            helper.playerBet(bet1);
            helper.playerBet(bet2);
            helper.playerBet(bet3);
            helper.playerBet(bet4);
            helper.playerBet(bet5);
            helper.playerBet(bet6);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            helper.playerTurn();
            helper.playerTurn();
            helper.playerTurn();
            helper.playerTurn();
            helper.playerTurn();
            helper.playerTurn();
            if (match.getGame().isVerdictOut()) {
                helper.checkIfFinishedProperly();
            } else {
                helper.playerTurn();
                helper.checkIfFinishedProperly();
            }
            match.getGame().nextRound(match.getGameType());
        }
        System.out.println("Dealer money: " + match.getGame().getDealer().getMoney());
        System.out.println("Player money: " + match.getGame().getPlayers().get(0).getMoney());
        helper.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });
    }


    @Test
    public void gameWithSixPlayersPlayer1LeavesBeforeBetEntersBeforeBet() {
        Helper helper = new Helper();
        helper.addCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER2_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER2_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER3_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER3_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER4_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER4_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER5_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER5_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER6_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER6_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.DEALER_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.DEALER_HAD_BJ_FROM_FIRST_HAND);
        match = new Match("aa", 7, GameType.COMPUTER, GamePrivacy.PUBLIC);
        helper.setMatch(match);
        helper.addUserToMatch(user1, 2);
        helper.addUserToMatch(user2, 3);
        helper.addUserToMatch(user3, 4);
        helper.addUserToMatch(user4, 5);
        helper.addUserToMatch(user5, 6);
        helper.addUserToMatch(user6, 7);
        helper.startGame();

        for (int i = 0; i<10000; i++) {

            if (i % 10 == 0) {
                helper.setPlayerMoneyAndBetBeforeSomeoneLeave();
                helper.playerLeaves(user1);
                helper.assertIfDebts("PLAYER_LEFT_ILLEGAL", user1);
                helper.assertIfGameIsInTheProperStateAfterSomeoneLeaveBeforeStart();
            } else if (i % 10 == 5) {
                helper.addUserToMatch(user1, 7);
                helper.assertIfPlayerArrivedProperly(match.getGame().getPlayerByEmail(user1.getEmail()));
            }
            helper.playerBet(bet1);
            helper.playerBet(bet2);
            helper.playerBet(bet3);
            helper.playerBet(bet4);
            helper.playerBet(bet5);
            helper.playerBet(bet6);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            for (int k = 0; k < match.getGame().getPlayers().size(); k++) {
                helper.playerTurn();
            }
            if (match.getGame().isVerdictOut()) {
                helper.checkIfFinishedProperly();
            } else {
                helper.playerTurn();
                helper.checkIfFinishedProperly();
            }
            match.getGame().nextRound(match.getGameType());
        }




        System.out.println("Dealer money: " + match.getGame().getDealer().getMoney());
        System.out.println("Player money: " + match.getGame().getPlayers().get(0).getMoney());
        helper.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });
    }

    @Test
    public void gameWithSixPlayersPlayer1LeavesAfterBettingEntersOnBet() {
        Helper helper = new Helper();
        helper.addCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER2_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER2_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER3_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER3_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER4_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER4_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER5_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER5_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER6_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER6_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.DEALER_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.DEALER_HAD_BJ_FROM_FIRST_HAND);
        match = new Match("aa", 7, GameType.COMPUTER, GamePrivacy.PUBLIC);
        helper.setMatch(match);
        helper.addUserToMatch(user1, 2);
        helper.addUserToMatch(user2, 3);
        helper.addUserToMatch(user3, 4);
        helper.addUserToMatch(user4, 5);
        helper.addUserToMatch(user5, 6);
        helper.addUserToMatch(user6, 7);
        helper.startGame();

        for (int i = 0; i<10000; i++) {
            helper.playerBet(bet1);
            if (i % 10 == 0) {
                helper.setPlayerMoneyAndBetBeforeSomeoneLeave();
                helper.playerLeaves(user1);
                helper.assertIfDebts("PLAYER_LEFT_ILLEGAL", user1);
                helper.assertIfGameIsInTheProperStateAfterSomeoneLeaveBeforeStart();
            } else if (i % 10 == 5) {
                helper.addUserToMatch(user1, 7);
                helper.assertIfPlayerArrivedProperly(match.getGame().getPlayerByEmail(user1.getEmail()));
            }
            helper.playerBet(bet2);
            helper.playerBet(bet3);
            helper.playerBet(bet4);
            helper.playerBet(bet5);
            helper.playerBet(bet6);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            for (int k = 0; k < match.getGame().getPlayers().size(); k++) {
                helper.playerTurn();
            }
            if (match.getGame().isVerdictOut()) {
                helper.checkIfFinishedProperly();
            } else {
                helper.playerTurn();
                helper.checkIfFinishedProperly();
            }
            match.getGame().nextRound(match.getGameType());
        }




        System.out.println("Dealer money: " + match.getGame().getDealer().getMoney());
        System.out.println("Player money: " + match.getGame().getPlayers().get(0).getMoney());
        helper.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });
    }

    @Test
    public void gameWithSixPlayersPlayer1LeavesBeforePlayingEntersBeforePlaying() {
        Helper helper = new Helper();
        helper.addCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER2_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER2_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER3_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER3_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER4_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER4_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER5_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER5_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER6_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER6_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.DEALER_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.DEALER_HAD_BJ_FROM_FIRST_HAND);
        match = new Match("aa", 7, GameType.COMPUTER, GamePrivacy.PUBLIC);
        helper.setMatch(match);
        helper.addUserToMatch(user1, 2);
        helper.addUserToMatch(user2, 3);
        helper.addUserToMatch(user3, 4);
        helper.addUserToMatch(user4, 5);
        helper.addUserToMatch(user5, 6);
        helper.addUserToMatch(user6, 7);
        helper.startGame();

        for (int i = 0; i<10000; i++) {

            helper.playerBet(bet1);
            helper.playerBet(bet2);
            helper.playerBet(bet3);
            helper.playerBet(bet4);
            helper.playerBet(bet5);
            helper.playerBet(bet6);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            if (i % 10 == 0) {
                helper.setPlayerMoneyAndBetBeforeSomeoneLeave();
                helper.playerLeaves(user1);
                helper.assertIfDebts("PLAYER_LEFT_ILLEGAL", user1);
                helper.assertIfGameIsInTheProperStateAfterSomeoneLeaveBeforeStart();
            } else if (i % 10 == 5) {
                helper.addUserToMatch(user1, 7);
                helper.assertIfPlayerArrivedProperly(match.getGame().getPlayerByEmail(user1.getEmail()));
            }
            for (int k = 0; k < match.getGame().getPlayers().size(); k++) {
                helper.playerTurn();
            }
            if (match.getGame().isVerdictOut()) {
                helper.checkIfFinishedProperly();
            } else {
                helper.playerTurn();
                helper.checkIfFinishedProperly();
            }
            match.getGame().nextRound(match.getGameType());
        }




        System.out.println("Dealer money: " + match.getGame().getDealer().getMoney());
        System.out.println("Player money: " + match.getGame().getPlayers().get(0).getMoney());
        helper.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });
    }

    @Test
    public void gameWithSixPlayersPlayer1LeavesAfterPlayingEntersAfterPlaying() {
        Helper helper = new Helper();
        helper.addCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER2_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER2_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER3_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER3_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER4_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER4_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER5_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER5_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER6_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER6_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.DEALER_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.DEALER_HAD_BJ_FROM_FIRST_HAND);
        match = new Match("aa", 7, GameType.COMPUTER, GamePrivacy.PUBLIC);
        helper.setMatch(match);
        helper.addUserToMatch(user1, 2);
        helper.addUserToMatch(user2, 3);
        helper.addUserToMatch(user3, 4);
        helper.addUserToMatch(user4, 5);
        helper.addUserToMatch(user5, 6);
        helper.addUserToMatch(user6, 7);
        helper.startGame();

        for (int i = 0; i<10000; i++) {

            helper.playerBet(bet1);
            helper.playerBet(bet2);
            helper.playerBet(bet3);
            helper.playerBet(bet4);
            helper.playerBet(bet5);
            helper.playerBet(bet6);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            if (i % 10 == 0) {
                helper.playerTurn();
                helper.setPlayerMoneyAndBetBeforeSomeoneLeave();
                helper.playerLeaves(user1);
                helper.assertIfDebts("PLAYER_LEFT_ILLEGAL", user1);
            } else if (i % 10 == 5) {
                helper.addUserToMatch(user1, 7);
                helper.assertIfPlayerArrivedProperly(match.getGame().getPlayerByEmail(user1.getEmail()));
            }
                while (!match.getGame().isVerdictOut()) {
                    helper.playerTurn();
                }
            helper.checkIfFinishedProperly();

            match.getGame().nextRound(match.getGameType());
        }




        System.out.println("Dealer money: " + match.getGame().getDealer().getMoney());
        System.out.println("Player money: " + match.getGame().getPlayers().get(0).getMoney());
        helper.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });
    }

    @Test
    public void gameWithSixPlayersPlayer1LeavesAfterVerdict() {
        Helper helper = new Helper();
        helper.addCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER2_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER2_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER3_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER3_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER4_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER4_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER5_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER5_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER6_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.PLAYER6_HAD_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.DEALER_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
        helper.addCustomAssertion(CustomAssertions.DEALER_HAD_BJ_FROM_FIRST_HAND);
        match = new Match("aa", 7, GameType.COMPUTER, GamePrivacy.PUBLIC);
        helper.setMatch(match);
        helper.addUserToMatch(user1, 2);
        helper.addUserToMatch(user2, 3);
        helper.addUserToMatch(user3, 4);
        helper.addUserToMatch(user4, 5);
        helper.addUserToMatch(user5, 6);
        helper.addUserToMatch(user6, 7);
        helper.startGame();

        for (int i = 0; i<10000; i++) {

            helper.playerBet(bet1);
            helper.playerBet(bet2);
            helper.playerBet(bet3);
            helper.playerBet(bet4);
            helper.playerBet(bet5);
            helper.playerBet(bet6);
            assertTrue(match.getGame().hasEveryoneBet());
            match.getGame().startRound(match);
            for (int k = 0; k < match.getGame().getPlayers().size(); k++) {
                helper.playerTurn();
            }
            if (match.getGame().isVerdictOut()) {
                helper.checkIfFinishedProperly();
            } else {
                helper.playerTurn();
                helper.checkIfFinishedProperly();
            }
            if (i % 10 == 0) {
                helper.setPlayerMoneyAndBetBeforeSomeoneLeave();
                helper.playerLeaves(user1);
                helper.assertIfDebts("PLAYER_LEFT_ILLEGAL", user1);
            } else if (i % 10 == 5) {
                helper.addUserToMatch(user1, 7);
                helper.assertIfPlayerArrivedProperly(match.getGame().getPlayerByEmail(user1.getEmail()));
            }
            match.getGame().nextRound(match.getGameType());
        }




        System.out.println("Dealer money: " + match.getGame().getDealer().getMoney());
        System.out.println("Player money: " + match.getGame().getPlayers().get(0).getMoney());
        helper.customAssertions.forEach((scenario, isTrue) -> {
            System.out.println(scenario + (isTrue ? ": TRUE" : ": FALSE"));
            assertTrue(isTrue);
        });
    }


}
