class PlayerUtils {
    static findPlayerByEmail = (email, players) => {
        for (const player of players) {
            if (player.email === email) return player;
        }
    }

    static findAndReplacePlayerByEmail = (email, players, replaceWithPlayer) => {
        for (let playerIdx = 0; playerIdx < players.length; playerIdx++) {
            const player = players[playerIdx];
            if (player.email === email) {
                players.splice(playerIdx, 1, replaceWithPlayer);
            }
        }
    }
}

export default PlayerUtils;