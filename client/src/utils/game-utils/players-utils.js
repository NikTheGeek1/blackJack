class PlayerUtils {
    static findPlayerByEmail = (email, players) => {
        for (const player of players) {
            if (player.email === email) return player;
        }
    }
}

export default PlayerUtils;