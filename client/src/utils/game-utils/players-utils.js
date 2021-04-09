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

    static getNextPlayerIfThereIsOne = (currentPlayerEmail, allPlayers) => {
        for (let playerIdx = 0; playerIdx < allPlayers.length; playerIdx++) {
            if (allPlayers[playerIdx].email === currentPlayerEmail) {
                if (playerIdx < (allPlayers.length - 1)) {
                    return allPlayers[playerIdx + 1];
                } else {
                    return -1;
                }
            }
        }
    }
}

export default PlayerUtils;