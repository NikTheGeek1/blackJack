import { useEffect, useState } from 'react';
import { useStore } from '../../hooks-store/store';
import './MatchPage.css';
import { initSocket } from '../../websockets/web-sockets-game-rep';
import URLs from '../../services/DEV-URLs';
import { SET_MATCH } from '../../hooks-store/stores/match-store';
import { SET_PLAYER } from '../../hooks-store/stores/player-store';
import { SET_PLAYER_CHOICE } from '../../hooks-store/stores/player-choice-store';
import PlayerUtils from '../../utils/game-utils/players-utils';
import Match from '../../models/matches/Match';
import PlayerStatus from '../../constants/PlayerStatus';
import { FormButton, FormInput } from '../../components/Form/components';
import Bet from '../../models/Bet';
import Chat from '../../components/Chat/Chat';
import GameInterface from '../../components/GameInterface/GameInterface';

let gameSocket;
const MatchPage = () => {
    const [globalState, dispatch] = useStore();
    const [betValue, setBetValue] = useState(0);
    const [screenDimensions, setScreenDimensions] = useState({ width: window.innerWidth, height: window.innerHeight });
    const match = globalState.matchState.matchObj;
    const thisPlayer = globalState.playerState.playerObj;

    useEffect(() => {
        window.addEventListener("resize", screenDimensionsHandler);

        return () => {
            window.removeEventListener("resize", screenDimensions);
        };
    }, []);

    useEffect(() => {
        gameSocket = initSocket();
    }, []);

    useEffect(() => {
        let updateGameSubscription;
        let playerChoiceSubscription;
        window.onbeforeunload = () => {
            leavingPageHandler([updateGameSubscription, playerChoiceSubscription]);
        };

        gameSocket.connect({}, frame => {
            updateGameSubscription = gameSocket.subscribe(URLs.UPDATE_GAME(match.matchName), (msg) => {
                const matchParsed = JSON.parse(msg.body);
                console.log(matchParsed, 'MatchPage.js', 'line: ', '31');
                dispatch(SET_MATCH, matchParsed);
                const thisPlayerUpdated = PlayerUtils.findPlayerByEmail(thisPlayer.email, matchParsed.game.allPlayersDealerFirst);
                dispatch(SET_PLAYER, thisPlayerUpdated);
            });

            playerChoiceSubscription = gameSocket.subscribe(URLs.PLAYER_CHOICE(match.matchName), (msg) => {
                const choiceParsed = JSON.parse(msg.body);
                dispatch(SET_PLAYER_CHOICE, choiceParsed);
            });
            
            gameSocket.send(URLs.ENTER_GAME(match.matchName), {}, "entered game");
        });

        return () => {
            leavingPageHandler([updateGameSubscription, playerChoiceSubscription]);
        };

    }, []);

    const screenDimensionsHandler = () => {
        setScreenDimensions({ width: window.innerWidth, height: window.innerHeight });
    };

    const leavingPageHandler = (subscriptions) => {
        for (const sub of subscriptions) {
            sub.unsubscribe();
        }
        gameSocket.send(URLs.LEAVE_GAME(match.matchName), {}, thisPlayer.email);
        gameSocket.disconnect();
    };

    const startHumansGameHandler = () => {
        gameSocket.send(URLs.START_GAME(match.matchName), {}, "starting game");
    };

    const betHandler = e => {
        e.preventDefault();
        // TODO: validate bet
        if (!!!betValue) return;
        const bet = new Bet(betValue, thisPlayer.email);
        gameSocket.send(URLs.PLACE_BET(match.matchName), {}, JSON.stringify(bet));
    };

    const stickHandler = () => {
        gameSocket.send(URLs.STICK(match.matchName), {}, "sticking");
    };

    const drawHandler = () => {
        gameSocket.send(URLs.DRAW(match.matchName), {}, "drawing");
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
            {/* {playersJSX} */}
            <GameInterface screenDimensions={screenDimensions}/>
            <Chat screenDimensions={screenDimensions}/>
        </div>
    );
};

export default MatchPage;