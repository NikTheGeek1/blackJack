import { useEffect, useState } from 'react';
import { useStore } from '../../hooks-store/store';
import './MatchPage.css';
import { SET_MATCH, PARTIALLY_SET_MATCH } from '../../hooks-store/stores/match-store';
import { SET_PLAYER } from '../../hooks-store/stores/player-store';
import { SET_PLAYER_CHOICE } from '../../hooks-store/stores/player-choice-store';
import Chat from '../../components/Chat/Chat';
import GameInterface from '../../components/GameInterface/GameInterface';
import GameSocketsManager from '../../websockets/GameSocketsManager';

let gameSocketManager;
const MatchPage = () => {
    const [globalState, dispatch] = useStore();
    const [screenDimensions, setScreenDimensions] = useState({ width: window.innerWidth, height: window.innerHeight });
    const match = globalState.matchState.matchObj;
    const thisPlayer = globalState.playerState.playerObj;

    useEffect(() => {
        window.addEventListener("resize", screenDimensionsHandler);
        if (match && thisPlayer) {
            gameSocketManager = new GameSocketsManager(
                matchParsed => dispatch(SET_MATCH, matchParsed, false),
                payload => dispatch(PARTIALLY_SET_MATCH, payload, false),
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

    return (
        <div className="match-page-container">
            {(gameSocketManager && match.game) && <GameInterface screenDimensions={screenDimensions} gameSocketManager={gameSocketManager} />}
            <Chat />
        </div>
    );
};

export default MatchPage;