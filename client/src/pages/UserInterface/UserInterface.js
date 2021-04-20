import { useStore } from '../../hooks-store/store';
import { SET_MATCH } from '../../hooks-store/stores/match-store';
import { SET_PLAYER } from '../../hooks-store/stores/player-store';
import { LOG_USER_OUT } from '../../hooks-store/stores/user-credential-store';
import './UserInterface.css';
import { initSocket } from '../../websockets/web-sockets-matches-rep';
import { useEffect, useState } from 'react';
import { addMatch, addUserToMatch } from '../../services/websocketsREST/match-services';
import { addLobbyUser, removeLobbyUser } from '../../services/websocketsREST/lobby-user-services';
import URLs from '../../services/DEV-URLs';
import { useHistory } from 'react-router';
import NewMatchForm from '../../components/NewMatch/NewMatch';
import JoinPrivateMatch from '../../components/UserInterface/JoinPrivateMatch/JoinPrivateMatch';
import ProfileWindow from '../../components/UserInterface/ProfileWindow/ProfileWindow';
import tabItems, { tabsValues } from '../../constants/userInterfaceTabItems';

let matchesSocket;

const UserInterface = () => {
    const joinMatchHandler = (matchName, matchPassword) => {
        // TODO: Add proper validation
        if (!matchName) return;
        if (matchPassword && matchPassword.trim() === '') return;
        addUserToMatch(matchName, matchPassword, globalState.userState.userObj.email,
            sucRes => goToMatchPage(sucRes),
            errRes => console.log(errRes))
    };
    const [globalState, dispatch] = useStore();
    const [matches, setMatches] = useState([]);
    const [UIBodyJSX, setUIBodyJSX] = useState(
        {
            label: tabItems.JOIN_GAME.label,
            component: <tabItems.JOIN_GAME.component matches={matches} joinMatchHandler={joinMatchHandler} />
        });
    const history = useHistory();

    useEffect(() => {
        matchesSocket = initSocket();
    }, []);

    useEffect(() => {
        let availableMatchesSubscription;
        matchesSocket.connect({}, frame => {
            availableMatchesSubscription = matchesSocket.subscribe(URLs.REPLY_TO_LIST_OF_MATCHES, (msg) => {
                const matchesParsed = JSON.parse(msg.body);
                setMatches(Object.values(matchesParsed));
            });
            matchesSocket.send(URLs.REQUEST_LIST_OF_MATCHES, {}, "requesting list of matches");
        });

        return () => {
            removeLobbyUser(globalState.userState.userObj.email, sucRes => console.log(sucRes), errRes => console.log(errRes));
            availableMatchesSubscription && availableMatchesSubscription.unsubscribe();
            matchesSocket.disconnect();
        };
    }, []);

    useEffect(() => {
        if (matchesSocket) {
            addLobbyUser(globalState.userState.userObj,
                sucRes => console.log(sucRes),
                errorRes => console.log(errorRes)
            );
        }
    }, []);



    const addMatchHandler = (match) => {
        addMatch(match, globalState.userState.userObj.email,
            resSucc => goToMatchPage(resSucc),
            resError => console.log(resError))
    };

    const goToMatchPage = (response) => {
        dispatch(SET_MATCH, response.match);
        dispatch(SET_PLAYER, response.player);
        history.push("/match");
    };

    const tabHandler = tab => {
        switch (tab) {
            case tabItems.JOIN_GAME.label:
                const joinGameJSX = <tabItems.JOIN_GAME.component matches={matches} joinMatchHandler={joinMatchHandler} />;
                const joinGameLabel = tabItems.JOIN_GAME.label;
                setUIBodyJSX({ label: joinGameLabel, component: joinGameJSX });
                break;
            case tabItems.CREATE_GAME.label:
                const createGameJSX = <tabItems.CREATE_GAME.component addMatchHandler={addMatchHandler} />;
                const createGameLabel = tabItems.CREATE_GAME.label;
                setUIBodyJSX({ label: createGameLabel, component: createGameJSX });
                break;
            case tabItems.FIND_PRIVATE_GAME.label:

                break;
            case tabItems.INFORMATIONS.label:

                break;

            default:
                break;
        }
    }

    const tabsJSX = tabsValues
        .map(tab => {
            const selectedTabClass = tab === UIBodyJSX.label && "selected-tab";
            return (
                <li className={"user-interface-tab-item " + selectedTabClass}
                    key={tab}
                    onClick={() => tabHandler(tab)}
                >
                    {tab}
                </li>
            );
        });

    return (
        // <>
        //     <p>this is the user interface page</p>
        //     <button onClick={() => dispatch(LOG_USER_OUT)}>Logout</button>
        //     <ProfileWindow />
        //     <NewMatchForm addMatchHandler={addMatchHandler} />
        //     <JoinPrivateMatch joinMatchHandler={joinMatchHandler} />
        //     <p>List of matches</p>
        //     <MatchesTable matches={matches} joinMatchHandler={joinMatchHandler} />
        // </>
        <section className="user-interface-section">
            <div className="user-interface-container">
                <div className="user-interface-header">
                    <ul className="user-interface-tabs-list">
                        {tabsJSX}
                    </ul>
                </div>
                <div className="user-interface-body">
                    {UIBodyJSX.component}
                </div>
            </div>
        </section>
    );
};

export default UserInterface;