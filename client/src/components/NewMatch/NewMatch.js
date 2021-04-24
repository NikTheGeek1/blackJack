import './NewMatch.css';
import { FormInput, FormButton, FormSelect, FormOption } from '../Form/components';
import { useState } from 'react';
import ErrorMessage from '../ErrorMessage/ErrorMessage';

const NewMatch = ({ addMatchHandler, errorMessage }) => {
    const [matchName, setMatchName] = useState('');
    const [maxNumberOfPlayers, setMaxNumberOfPlayers] = useState('Maximum number of players: ');
    const [privacy, setPrivacy] = useState('hide');
    const [gameType, setGameType] = useState('hide');
    const [matchPassword, setMatchPassword] = useState('');

    const submitFormHandler = e => {
        e.preventDefault();
        // TODO: elaborate validation
        if (!matchName || !privacy || !gameType) return;
        let match;
        match = {
            matchName: matchName,
            maxNumberOfPlayers: maxNumberOfPlayers,
            privacy: privacy.toUpperCase(),
            gameType: gameType.toUpperCase(),
        };
        if (privacy === "Private") {
            if (!matchPassword) return;
            match["matchPassword"] = matchPassword;
        }
        addMatchHandler(match);
    };

    return (
        <div className="new-match-container">
            <form onSubmit={submitFormHandler} className="new-match-form">
                <ErrorMessage message={errorMessage} />
                <FormInput
                    title="-- Game name --"
                    type="text"
                    onChange={(e) => setMatchName(e.target.value)}
                    value={matchName}
                    infoMessage="Game name should be unique and maximum 10 characters."
                    required
                />
                <FormInput
                    title="-- Maximum number of players --"
                    type="number"
                    onChange={e => setMaxNumberOfPlayers(e.target.value)}
                    value={maxNumberOfPlayers}
                    otherProps={{ min: 2, max: 7 }}
                    infoMessage="Maximum number of players should be between 2 and 7 inclusive."
                    required
                />
                <div>
                    <FormSelect
                        defaultValue="-- Privacy --"
                        onChange={e => setPrivacy(e.target.getAttribute('rel'))}
                        value={privacy}
                        valuesArray={["Private", "Public"]}
                        infoMessage="Public games are accessible by everyone, while private require password."
                    />
                </div>
                {privacy === "Private" &&
                    <FormInput
                        title="Password: "
                        type="text"
                        onChange={(e) => setMatchPassword(e.target.value)}
                        value={matchPassword}
                        infoMessage="Share this password to people who want to join."
                        required
                    />
                }
                <div>
                    <FormSelect
                        defaultValue="-- Type --"
                        onChange={e => setGameType(e.target.getAttribute('rel'))}
                        value={gameType}
                        valuesArray={["Computer", "Humans"]}
                        infoMessage="Human games consist only by humans, where the dealer is a human and changes every two busts. In computer games the dealer is always the computer, normal players are humans."
                    />
                </div>
                <FormButton title="Create game" />
            </form>
        </div>
    );
};

export default NewMatch;