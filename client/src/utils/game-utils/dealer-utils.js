export const isThereADealer = (players) => {
    for (const player of players) {
        if (player.isDealer) return true;
    }
    return false;
};

export const getDealer = (players) => {
    for (const player of players) {
        if (player.isDealer) return player;
    }
    return false;
};