package com.blackjack.server.models.game;

import com.blackjack.server.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Dealer extends Player {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int numOfBusts;

    public Dealer(Player player) {
        super(player);
        numOfBusts = 0;
    }

    public Dealer(User user) {
        super(user);
        numOfBusts = 0;
    }

    public int getNumOfBusts() {
        return numOfBusts;
    }

    public void setNumOfBusts(int numOfBusts) {
        this.numOfBusts = numOfBusts;
    }

    @Override
    public void statusAfterPlaying () {
        super.statusAfterPlaying();
        if (getStatus() == PlayerStatus.BUSTED) numOfBusts++;
    }

}
