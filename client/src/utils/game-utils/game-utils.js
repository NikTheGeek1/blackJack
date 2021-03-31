import PlayerStatus from "../../constants/PlayerStatus";

export const isPlayerAfterInitialAnimation = player => {
    // TODO: You'll probably need to refactor this for when a new player joins they will
    // not be able to see game updates
    return player.status !== PlayerStatus.BETTING && player.status !== PlayerStatus.WAITING_GAME;
};


