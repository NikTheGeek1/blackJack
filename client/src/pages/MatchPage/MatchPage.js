import { useEffect, useState } from 'react';
import { useStore } from '../../hooks-store/store';
import './MatchPage.css';
import URLs from '../../services/DEV-URLs';
import { SET_MATCH } from '../../hooks-store/stores/match-store';
import { SET_PLAYER } from '../../hooks-store/stores/player-store';
import { SET_PLAYER_CHOICE } from '../../hooks-store/stores/player-choice-store';
import Match from '../../models/matches/Match';
import PlayerStatus from '../../constants/PlayerStatus';
import { FormButton, FormInput } from '../../components/Form/components';
import Chat from '../../components/Chat/Chat';
import GameInterface from '../../components/GameInterface/GameInterface';
import GameSocketsManager from '../../websockets/GameSocketsManager';

let gameSocketManager;
const MatchPage = () => {
    const [globalState, dispatch] = useStore();
    const [betValue, setBetValue] = useState(0);
    const [screenDimensions, setScreenDimensions] = useState({ width: window.innerWidth, height: window.innerHeight });
    const match = globalState.matchState.matchObj;
    const thisPlayer = globalState.playerState.playerObj;
    useEffect(() => {
        window.addEventListener("resize", screenDimensionsHandler);
        if (match && thisPlayer) {
            gameSocketManager = new GameSocketsManager(
                matchParsed => dispatch(SET_MATCH, matchParsed, false),
                player => dispatch(SET_PLAYER, player, false),
                choice => dispatch(SET_PLAYER_CHOICE, choice),
                match.matchName,
                thisPlayer.email
            );
            gameSocketManager.connect();
        }
        return () => {
            window.removeEventListener("resize", screenDimensions);
        };
    }, []);

    useEffect(() => {
        window.onbeforeunload = () => {
            gameSocketManager.cleanUp();
        };
        return () => {
            gameSocketManager.cleanUp();
        };
    }, []);

    const screenDimensionsHandler = () => {
        setScreenDimensions({ width: window.innerWidth, height: window.innerHeight });
    };


    // console.log(match, 'MatchPage.js', 'line: ', '86');
    // const playersJSX = match.game?.allPlayersDealerFirst.map(player => {
    //     let turnControlsJSX;
    //     // TODO: extract all these if statements into functions
    //     if (thisPlayer.status === PlayerStatus.WAITING_GAME) {
    //         turnControlsJSX = <h3>Please wait for the creator to start the game</h3>;
    //     }
    //     if (thisPlayer.status === PlayerStatus.WAITING_GAME &&
    //         (thisPlayer.isDealer || match.gameType === "COMPUTER")) { // TODO: make a GameType enum
    //         turnControlsJSX = <h3>Please wait for at least one more player</h3>;
    //         if (match.game.players.length) { // TODO: only the creator should be seeing the start game button
    //             console.log(match.game.players, 'I AM HERE BECAUSE THE START GAME BUTTON APPEARS EVEN WITH ONLY 1 PLAYER IN THE GAME', 'line: ', '86');
    //             turnControlsJSX = (
    //                 <button onClick={startHumansGameHandler}>Start game</button>
    //             );
    //         }
    //     }
    //     if (thisPlayer.status === PlayerStatus.WAITING_TURN) {
    //         turnControlsJSX = <h3>Please wait your turn</h3>;
    //     }
    //     if (thisPlayer.status === PlayerStatus.PLAYING) {
    //         turnControlsJSX = (
    //             <>
    //                 <button onClick={stickHandler}>Stick</button>
    //                 <button onClick={drawHandler}>Draw</button>
    //             </>
    //         );
    //     }
    //     if (thisPlayer.status === PlayerStatus.BETTING) {
    //         turnControlsJSX = (
    //             <form onSubmit={betHandler}>
    //                 <FormInput type="number" title="Bet" otherProps={{ min: 1, max: 10 }} value={betValue} onChange={e => setBetValue(e.target.value)} />
    //                 <FormButton title="Bet" />
    //             </form>
    //         );
    //     }

    //     return (
    //         <div key={player.email} style={{ backgroundColor: player.status === PlayerStatus.PLAYING && "red" }}>
    //             <div>{player.name}</div>
    //             <div>Bet: {player.bet}</div>
    //             <div>Money: {player.money}</div>
    //             <div>Is dealer? {player.isDealer ? "Yes" : "No"}</div>
    //             <div>Status: {player.status}</div>
    //             <div>Reveald Cards: {player.revealedCards?.map(card => card.suit + " " + card.rank + " || ")}</div>
    //             {thisPlayer.email === player.email && turnControlsJSX}
    //             <hr />
    //         </div>
    //     );
    // });

    return (
        <div className="match-page-container">
            {(gameSocketManager && match.game) && <GameInterface screenDimensions={screenDimensions} gameSocketManager={gameSocketManager}/>}
            <Chat screenDimensions={screenDimensions} />
        </div>
    );
};

export default MatchPage;