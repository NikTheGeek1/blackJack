import './NewMatchForm.css';
import { FormInput, FormButton, FormSelect, FormOption } from '../../../components/Form/components';
import { useState } from 'react';
import Match from '../../../models/matches/Match';

const NewMatchForm = ({ addMatchHandler }) => {
    const [matchName, setMatchName] = useState('');
    const [maxNumberOfPlayers, setMaxNumberOfPlayers] = useState(2);
    const [privacy, setPrivacy] = useState('');
    const [gameType, setGameType] = useState('');

    const submitFormHandler = e => {
        e.preventDefault();
        // TODO: elaborate validation
        if (!matchName || !privacy || !gameType) return;
        const match = {
            matchName: matchName,
            maxNumberOfPlayers: maxNumberOfPlayers,
            privacy: privacy.toUpperCase(),
            gameType: gameType.toUpperCase(),
        };
        addMatchHandler(match);
    };

    return (
        <form onSubmit={submitFormHandler}>
            <FormInput title="Game name" type="text" onChange={(e) => setMatchName(e.target.value)} value={matchName} />
            <FormInput title="Maximum number of players" type="number" onChange={e => setMaxNumberOfPlayers(e.target.value)} value={maxNumberOfPlayers} otherProps={{ min: 2, max: 7 }} />
            <FormSelect defaultValue="Privacy" onChange={e => setPrivacy(e.target.value)} value={privacy}>
                <FormOption title="Private" />
                <FormOption title="Public" />
            </FormSelect>

            <FormSelect defaultValue="Type" onChange={e => setGameType(e.target.value)} value={gameType}>
                <FormOption title="Computer" />
                <FormOption title="Humans" />
            </FormSelect>
            <FormButton title="Create game" />
        </form>
    );
};

export default NewMatchForm;