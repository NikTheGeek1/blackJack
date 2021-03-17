package com.blackjack.server.Game_Computer;

import com.blackjack.server.models.User;
import com.blackjack.server.models.game.*;
import com.blackjack.server.models.match.Match;

import java.util.HashMap;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

public class Helper {

    private Match match;
    public HashMap<CustomAssertions, Boolean> customAssertions = new HashMap<CustomAssertions, Boolean>();

    Dealer dealer;
    public LinkedList<Player> players;
    HashMap<String, Double> playerMoneyBeforeSomeoneLeave = new HashMap<>();
    HashMap<String, Double> playerBetBeforeSomeoneLeave = new HashMap<>();

    public void addCustomAssertion(CustomAssertions assertion) {
        customAssertions.put(assertion, false);
    }

    public void passCustomAssertion(CustomAssertions assertion) {
        if (!customAssertions.containsKey(assertion)) {
            fail("Assertion doesn't exists");
        }
        customAssertions.put(assertion, true);
    }

    public void suppressCustomAssertion(CustomAssertions assertion) {
        customAssertions.remove(assertion);
    }

    public void addUserToMatch(User user, int totalNumOfPlayers) {
        match.addUser(user);
        GamePrep.setUpGameOrAddPlayer(match);
        players.add(match.getGame().getPlayerByEmail(user.getEmail()));
        assertNotNull(match.getGame());
        assertEquals(totalNumOfPlayers, match.getGame().getAllPlayersDealerFirst().size());
        assertEquals(totalNumOfPlayers-1, match.getGame().getPlayers().size());
        assertEquals(PlayerStatus.WAITING_GAME, match.getGame().getPlayerByEmail(user.getEmail()).getStatus());
    }


    public void setMatch(Match match) {
        this.match = match;
        this.players = new LinkedList<>();
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
//        this.players.addAll(match.getGame().getPlayers());
        this.dealer = match.getGame().getDealer();
    }

    public void playerBet(Bet bet) {
        // first player after dealer bets. their bet value should
        // be equals to what they bet. they should be in a WAITING_TURN status
        Player player = match.getGame().getPlayerByEmail(bet.getPlayerEmail());
        if (player == null || player.getCards().isEmpty()) return; // perhaps the player just left or just arrived
        match.getGame().placeBet(bet.getPlayerEmail(), bet.getBetValue());
        assertEquals(PlayerStatus.WAITING_TURN, player.getStatus());
        assertEquals(bet.getBetValue(), player.getBet());
        assertEquals(1, player.getRevealedCards().size());
    }

    public void playerTurn() {
        Player nextPlayer = match.getGame().grabPlayingPlayer();
        if (match.getGame().getPlayerWhoJustGotDealtBlackJack() != null) {
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                if (nextPlayer.getEmail().equals(player.getEmail())) {
                    switch (i) {
                        case 0: {
                            passCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
                            break;
                        }
                        case 1: {
                            passCustomAssertion(CustomAssertions.PLAYER2_HAD_BJ_FROM_FIRST_HAND);
                            break;
                        }
                        case 2: {
                            passCustomAssertion(CustomAssertions.PLAYER3_HAD_BJ_FROM_FIRST_HAND);
                            break;
                        }
                        case 3: {
                            passCustomAssertion(CustomAssertions.PLAYER4_HAD_BJ_FROM_FIRST_HAND);
                            break;
                        }
                        case 4: {
                            passCustomAssertion(CustomAssertions.PLAYER5_HAD_BJ_FROM_FIRST_HAND);
                            break;
                        }
                        case 5: {
                            passCustomAssertion(CustomAssertions.PLAYER6_HAD_BJ_FROM_FIRST_HAND);
                            break;
                        }
                    }
                    passCustomAssertion(CustomAssertions.PLAYER1_HAD_BJ_FROM_FIRST_HAND);
                    assertEquals(player, match.getGame().getPlayerWhoJustGotDealtBlackJack());
                    assertEquals(PlayerStatus.BLACKJACK, player.getStatus());
                }
            }
            if (nextPlayer.getEmail() == dealer.getEmail()) {
                passCustomAssertion(CustomAssertions.DEALER_HAD_BJ_FROM_FIRST_HAND);
                assertEquals(dealer, match.getGame().getPlayerWhoJustGotDealtBlackJack());
                assertEquals(PlayerStatus.BLACKJACK, dealer.getStatus());
            }

            match.getGame().nextTurn(match);
        } else {
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                if (player != null && nextPlayer.getEmail() == player.getEmail()) {
                    switch (i) {
                        case 0:
                            passCustomAssertion(CustomAssertions.PLAYER1_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
                            break;
                        case 1:
                            passCustomAssertion(CustomAssertions.PLAYER2_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
                            break;
                        case 2:
                            passCustomAssertion(CustomAssertions.PLAYER3_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
                            break;
                        case 3:
                            passCustomAssertion(CustomAssertions.PLAYER4_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
                            break;
                        case 4:
                            passCustomAssertion(CustomAssertions.PLAYER5_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
                            break;
                        case 5:
                            passCustomAssertion(CustomAssertions.PLAYER6_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
                            break;
                    }
                    assertEquals(PlayerStatus.PLAYING, player.getStatus());
                }
            }

            if (nextPlayer.getEmail() == dealer.getEmail()) {
                passCustomAssertion(CustomAssertions.DEALER_DID_NOT_HAVE_BJ_FROM_FIRST_HAND);
                assertEquals(PlayerStatus.PLAYING, dealer.getStatus());
                sendDealerNextMove(match);
            } else {
                // draws
                while (nextPlayer.cardTotal() < 17) {
                    match.getGame().draws(match);
                }
                if (nextPlayer.cardTotal() > 16 && nextPlayer.cardTotal() < 21) {
                    // sticks
                    match.getGame().sticks();
                    match.setHasSimulationStared(true);
                }
            }



        }
    }

    private void sendDealerNextMove(Match match) {
        match.getGame().nextSimulatedTurn(match);
        if (match.getHasSimulationStared()) {
            sendDealerNextMove(match);
        }
    }

    public void checkIfFinishedProperly () {
        for (Player player : players) {
            if (player.getStatus() != PlayerStatus.WON &&
                    player.getStatus() != PlayerStatus.LOST ) {
                if (player.getStatus() == PlayerStatus.WAITING_GAME) continue;
                fail("PLAYER " + player.getEmail() + " SHOULD EITHER BE LOST OR WON BUT WAS: " + player.getStatus());
            }
        }
        if (dealer.getStatus() != PlayerStatus.STICK &&
                dealer.getStatus() != PlayerStatus.BLACKJACK &&
                dealer.getStatus() == PlayerStatus.WAITING_GAME) {
            fail("DEALER SHOULD EITHER STICK OR BJ BUT WAS: " + dealer.getStatus());
        }
        assertTrue(match.getGame().isVerdictOut());
    }





    public void setPlayerMoneyAndBetBeforeSomeoneLeave() {
        match.getGame().getPlayers().forEach(player -> playerMoneyBeforeSomeoneLeave.put(player.getEmail(), player.getMoney()));
        playerMoneyBeforeSomeoneLeave.put(match.getGame().getDealer().getEmail(), match.getGame().getDealer().getMoney());
        match.getGame().getPlayers().forEach(player -> playerBetBeforeSomeoneLeave.put(player.getEmail(), player.getBet()));
        playerBetBeforeSomeoneLeave.put(match.getGame().getDealer().getEmail(), match.getGame().getDealer().getBet());
    }
    public double getTotalSumOfBets() {
        double total = 0;
        for(String playerEmail : playerBetBeforeSomeoneLeave.keySet()) {
            total += playerBetBeforeSomeoneLeave.get(playerEmail);
        }
        return total;
    }
    public void assertIfDebts (String type, User leaver) {
        switch (type) {
            case "DEALER_LEFT_ILLEGAL":{
                match.getGame().getPlayers().forEach(player -> {
                    assertEquals(
                            playerMoneyBeforeSomeoneLeave.get(player.getEmail()) + playerBetBeforeSomeoneLeave.get(player.getEmail()),
                            player.getMoney());
                });
                assertEquals(
                        playerMoneyBeforeSomeoneLeave.get(leaver.getEmail()) - getTotalSumOfBets(),
                        leaver.getMoney());
                break;
            }
            case "PLAYER_LEFT_ILLEGAL": {
                assertEquals(
                        playerMoneyBeforeSomeoneLeave.get(dealer.getEmail()) + playerBetBeforeSomeoneLeave.get(leaver.getEmail()),
                        dealer.getMoney());
                assertEquals(
                        playerMoneyBeforeSomeoneLeave.get(leaver.getEmail()) - playerBetBeforeSomeoneLeave.get(leaver.getEmail()),
                        leaver.getMoney());
                break;
            }
            case "PLAYER_LEFT_LEGAL": {
                match.getGame().getPlayers().forEach(player -> {
                    assertEquals(
                            playerMoneyBeforeSomeoneLeave.get(player.getEmail()),
                            player.getMoney());
                });
                assertEquals(
                        playerMoneyBeforeSomeoneLeave.get(match.getGame().getDealer().getEmail()),
                        match.getGame().getDealer().getMoney());
                break;
            }
            default:
                fail("WRONG TYPE");
        }
    }


    public void assertIfGameIsInTheProperStateAfterSomeoneLeaveBeforeStart() {
        for (Player player : match.getGame().getPlayers()) {
            assertNotEquals(PlayerStatus.WAITING_GAME, player.getStatus());
            assertEquals(2, player.getCards().size());
        }
        assertNotEquals(PlayerStatus.WAITING_GAME, match.getGame().getDealer().getStatus());
        assertEquals(2, match.getGame().getDealer().getCards().size());
    }


    public void assertIfPlayerArrivedProperly(Player newComer) {
                assertEquals(PlayerStatus.WAITING_GAME, newComer.getStatus());
                assertFalse(newComer.getIsDealer());
                assertEquals(0, newComer.getCards().size());
                assertEquals(0, newComer.getRevealedCards().size());
                assertEquals(0, newComer.getBet());
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
        assertEquals(0, player.getBet());
    }


    private void removePlayerByEmail(String email) {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if(player.getEmail().equals(email)) {
                players.remove(i);
            }
        }
    }
    public void playerLeaves(User player) {

        removePlayerByEmail(player.getEmail());
        GamePrep.dropOutManager(match, player.getEmail());
    }
}
