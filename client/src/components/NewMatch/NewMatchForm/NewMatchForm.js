import './NewMatchForm.css';
import { FormInput, FormButton, FormSelect, FormOption } from '../../../components/Form/components';
import { useState } from 'react';
import Match from '../../../models/matches/Match';

const NewMatchForm = ({ addMatchHandler }) => {
    const [gameName, setGameName] = useState('');
    const [maxNumOfPlayers, setMaxNumOfPlayers] = useState(2);
    const [gamePrivacy, setGamePrivacy] = useState('');
    const [gameType, setGameType] = useState('');

    const submitFormHandler = e => {
        e.preventDefault();
        // TODO: elaborate validation
        if (!gameName || !gamePrivacy || !gameType) return;
        const match = new Match(
            gameName,
            maxNumOfPlayers,
            gameType,
            gamePrivacy
        );
        addMatchHandler(match);
    };

    return (
        <form onSubmit={submitFormHandler}>
            <FormInput title="Game name" type="text" onChange={(e) => setGameName(e.target.value)} value={gameName} />
            <FormInput title="Maximum number of players" type="number" onChange={e => setMaxNumOfPlayers(e.target.value)} value={maxNumOfPlayers} otherProps={{ min: 2, max: 7 }} />
            <FormSelect defaultValue="Privacy" onChange={e => setGamePrivacy(e.target.value)} value={gamePrivacy}>
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