package com.blackjack.server.models.game;

import com.blackjack.server.models.User;
import com.blackjack.server.utils.Player.TokenUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Player extends User  {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Card> cards;
    private List<Card> displayedCards;
    private HashMap<String, Integer> tokens;
    private HashMap<String, Integer> betTokens;
    private boolean isDealer;
    private PlayerStatus status;

    public Player(User user) {
        super(user);
        status = PlayerStatus.WAITING_GAME;
        isDealer = false;
        cards = new ArrayList<>();
        displayedCards = new ArrayList<>();
        tokens = TokenUtils.moneyToTokens(user.getMoney());
        betTokens = TokenUtils.moneyToTokens(0);
    }

    public Player (Player player) {
        super(player);
        this.status = player.getStatus();
        this.isDealer = player.getIsDealer();
        this.cards = player.getCards();
        this.displayedCards = player.displayedCards;
        this.tokens = player.getTokens();
        this.betTokens = player.getBetTokens();
    }

    public Player() {
        super();
        status = PlayerStatus.WAITING_GAME;
        isDealer = false;
        cards = new ArrayList<>();
        displayedCards = new ArrayList<>();
        this.tokens = TokenUtils.moneyToTokens(0);
        this.betTokens = TokenUtils.moneyToTokens(0);
    }

    public HashMap<String, Integer> getTokens() {
        return tokens;
    }

    public void setTokens(HashMap<String, Integer> tokens) {
        this.tokens = tokens;
    }

    public void setTokens(int money) {
        this.tokens = TokenUtils.moneyToTokens(money);
    }

    public void resetCards() {
        this.cards.clear();
        this.displayedCards.clear();
    }

    public List<Card> getDisplayedCards() {
        return displayedCards;
    }

    public void setDisplayedCards() {
        List<Card> dispCards = new ArrayList<>();
        for (Card card : cards) {
            if (card.getVisibility() == CardVisibility.REVEALED) {
                dispCards.add(card);
            }
            else {
                Card hiddenCardCopy = new Card(card.getSuit(), card.getRank());
                dispCards.add(hiddenCardCopy);
            }
        }
        this.displayedCards = dispCards;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public List<Card> getRevealedCards() {
        List<Card> revCards = this.displayedCards.stream()
                .filter(card -> card.getVisibility() == CardVisibility.REVEALED)
                .collect(Collectors.toList());
        return revCards;
    }


    public boolean getIsDealer() {
        return isDealer;
    }

    public void setIsDealer(boolean isDealer) {
        this.isDealer = isDealer;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
        if (status == PlayerStatus.PLAYING) {
            this.hiddenCard().setVisibility(CardVisibility.REVEALED);
            this.setDisplayedCards();
            statusAfterPlaying();
        }
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setBetTokens(HashMap<String, Integer> betTokens) {
        if ((TokenUtils.tokensToMoney(this.getTokens()) - TokenUtils.tokensToMoney(betTokens)) < 0) throw new ArithmeticException("Not enough tokens");
            this.betTokens = new HashMap<>(betTokens);
            this.decreaseTokens(betTokens);
    }

    public void setBetTokens(int money) {
        if ((TokenUtils.tokensToMoney(this.getTokens()) - money) < 0) throw new ArithmeticException("Not enough tokens");
        this.decreaseTokens(TokenUtils.moneyToTokens(money));
        this.betTokens = TokenUtils.moneyToTokens(money);
    }

    public void increaseTokens(HashMap<String, Integer> tokens) {
        for (String tokenColumn : this.tokens.keySet()) {
            this.tokens.put(tokenColumn, this.tokens.get(tokenColumn) + tokens.get(tokenColumn));
        }
    }

    public void lostBet(HashMap<String, Integer> betTokens) {
        if (this.isDealer) {
            this.decreaseTokens(betTokens);
        } else {
            this.betTokens = TokenUtils.moneyToTokens(0);
        }
    }

    public void wonBet(HashMap<String, Integer> betTokens) {
        if (this.isDealer) {
            this.increaseTokens(betTokens);
        } else {
            int wonMoney = TokenUtils.tokensToMoney(betTokens) * 2;
            this.increaseTokens(TokenUtils.moneyToTokens(wonMoney));
        }
        this.betTokens = TokenUtils.moneyToTokens(0);
    }

    public void decreaseTokens(HashMap<String, Integer> tokens) {
        for (String tokenColumn : this.tokens.keySet()) {
            this.tokens.put(tokenColumn, this.tokens.get(tokenColumn) - tokens.get(tokenColumn));
        }
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public HashMap<String, Integer> getBetTokens() {
        return betTokens;
    }

    public int cardTotal() {
        int total = 0;
        for (Card card : cards) {
            total += card.getRank().getValue();
        }
        return total;
    }

    public void prepareForNextRound() {
        this.betTokens = TokenUtils.moneyToTokens(0);
        status = PlayerStatus.BETTING;
    }

    public void addCard(Card card) {
        cards.add(card);
        handleAces();
        setDisplayedCards();
    }

    public void statusAfterPlaying () {
        if (this.cardTotal() == 21) this.setStatus(PlayerStatus.BLACKJACK);
        if (this.cardTotal() > 21) this.setStatus(PlayerStatus.BUSTED);
    }

    public void drawCard(Card card) {
        this.addCard(card);
        statusAfterPlaying();
    }

    public Card hiddenCard() {
        for (Card card : cards) {
            if (card.getVisibility() == CardVisibility.HIDDEN) return card;
        }
        return null;
    }

    public Card firstRevealedCard() {
        for (Card card : cards) {
            if (card.getVisibility() == CardVisibility.REVEALED) return card;
        }
        return null;
    }

    private void handleAces() {
        for (Card card : cards) {
            if (card.getRank() == Rank.ACE11 && cardTotal() > 21) card.setRank(Rank.ACE1);
        }
    }


}
