package com.blackjack.server.models.game;

import java.util.HashMap;
import java.util.List;

public class GameRound {
    private static HashMap<String, Player> playerWon(Player dealer, Player player) {
        HashMap<String, Player> results = new HashMap<>();
        results.put("Winner", player);
        results.put("Loser", dealer);
        return results;
    }

    private static HashMap<String, Player> dealerWon(Player dealer, Player player) {
        HashMap<String, Player> results = new HashMap<>();
        results.put("Winner", dealer);
        results.put("Loser", player);
        return results;
    }

    public static HashMap<String, Player> whoWon(Player dealer, Player player) {
        if (dealer.getStatus() == PlayerStatus.BLACKJACK) {
            if (player.getStatus() == PlayerStatus.BLACKJACK) {
                if (dealer.getCards().size() <= player.getCards().size()) return dealerWon(dealer, player);
                if (dealer.getCards().size() > player.getCards().size()) return playerWon(dealer, player);
            }
            return dealerWon(dealer, player);
        }

        if (dealer.getStatus() == PlayerStatus.STICK) {
            if (player.getStatus() == PlayerStatus.BLACKJACK) return playerWon(dealer, player);
            if (player.getStatus() == PlayerStatus.BUSTED) return dealerWon(dealer, player);
            if (player.getStatus() == PlayerStatus.STICK) {
                if (dealer.getCardTotal() >= player.getCardTotal()) return dealerWon(dealer, player);
                if (dealer.getCardTotal() < player.getCardTotal()) return playerWon(dealer, player);
            }
        }

        if (dealer.getStatus() == PlayerStatus.BUSTED && player.getStatus() == PlayerStatus.BUSTED) return dealerWon(dealer, player);
        return playerWon(dealer, player);
    }
}
