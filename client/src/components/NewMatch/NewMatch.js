import './NewMatch.css';
import { FormInput, FormButton, FormSelect, FormOption } from '../Form/components';
import { useState } from 'react';

const NewMatch = ({ addMatchHandler }) => {
    const [matchName, setMatchName] = useState('');
    const [maxNumberOfPlayers, setMaxNumberOfPlayers] = useState('Maximum number of players: ');
    const [privacy, setPrivacy] = useState('');
    const [gameType, setGameType] = useState('');
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
                <FormInput title="Game name: " type="text" onChange={(e) => setMatchName(e.target.value)} value={matchName} required />
                <FormInput title="Maximum number of players: " type="number" onChange={e => setMaxNumberOfPlayers(e.target.value)} value={maxNumberOfPlayers} otherProps={{ min: 2, max: 7 }} required />
                <div>
                <FormSelect defaultValue="Privacy" onChange={e => setPrivacy(e.target.value)} value={privacy}>
                    <FormOption title="Private" />
                    <FormOption title="Public" />
                </FormSelect>
                </div>
                {privacy === "Private" &&
                    <FormInput title="Password: " type="text" onChange={(e) => setMatchPassword(e.target.value)} value={matchPassword} required />
                }
                <div>
                <FormSelect defaultValue="Type" onChange={e => setGameType(e.target.value)} value={gameType}>
                    <FormOption title="Computer" />
                    <FormOption title="Humans" />
                </FormSelect>
                </div>
                <FormButton title="Create game" />
            </form>
        </div>
    );
};

export default NewMatch;