import { useState } from 'react';
import './MatchesTable.css';

const MatchesTalbe = ({ matches, joinMatchHandler }) => {

    const [searchInput, setSearchInput] = useState('');

    const matchesJSX = matches.map(match => {
        // applying filter
        if (match.matchName.includes(searchInput)) {
            return (
                <div key={match.matchName}>
                    <p>{match.matchName} max players: {match.maxNumberOfPlayers} players num: {match.users.length} duration: {match.duration}</p>
                    <button onClick={() => joinMatchHandler(match.matchName)}>Join</button>
                </div>
            );
        }
    });

    return (
        <>
            <div>
                <label htmlFor="search-for-matches-input">Search: </label>
                <input type="text" id="search-for-matches-input" value={searchInput} onChange={e => setSearchInput(e.target.value)} />
            </div>
            {matchesJSX}
        </>
    )
};

export default MatchesTalbe;