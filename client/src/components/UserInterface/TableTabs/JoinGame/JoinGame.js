import { useState } from 'react';
import { FormButton, FormInput } from '../../../Form/components';
import './JoinGame.css';

const JoinGame = ({ matches, joinMatchHandler }) => {
    const [searchInput, setSearchInput] = useState('');
    const [selectedMatch, setSelectedMatch] = useState('');

    const searchInputHandler = e => {
        setSelectedMatch('');
        setSearchInput(e.target.value);
    };

    const matchesJSX = matches.map((match, i) => {
        // applying filter
        if (match.matchName.includes(searchInput)) {
            const selectedMatchClass = match.matchName === selectedMatch && ' selected-match ';
            const rowStyle = i % 2 === 0 ? ' game-table-even-row ' : ' game-table-odd-row ';
            return (
                <div className={"joing-game-table-row " + selectedMatchClass + rowStyle} onClick={() => setSelectedMatch(match.matchName)} key={match.matchName}>
                    <p>{match.matchName}</p> <p>{match.users.length}/{match.maxNumberOfPlayers}</p> <p>{match.duration}</p> <p>{match.gameType}</p>
                </div>
            );
        }
    });

    return (
        <section className="join-game-section">
            <div className="join-game-search-container">
                <FormInput title="-- Search game --" value={searchInput} onChange={searchInputHandler}/>
            </div>
            <div className="join-game-matches-table">
                <div className="join-game-table-column-names">
                    <p>Name</p> <p>Players</p> <p>Duration</p> <p>Type</p>
                </div>
                <div className="joing-game-table-rows-container">
                    {matchesJSX}
                </div>
            </div>
            <form className="joing-game-join-form" onSubmit={e=> joinMatchHandler(e, selectedMatch)}>
                <FormButton title="Join game" otherProps={{disabled: !!!selectedMatch}} extraClasses={!selectedMatch && 'join-game-btn-disabled'}/>
            </form>
        </section>
    )
};

export default JoinGame;