import './JoinPrivateMatch.css';
import { FormButton, FormInput } from '../../Form/components';
import { useState } from 'react';

const JoinPrivateMatch = ({ joinMatchHandler }) => {

    const [name, setName] = useState('');
    const [password, setPassword] = useState('');
    const onJoinPrivateMatch = (e) => {
        e.preventDefault();
        joinMatchHandler(name, password);
    };

    return (
        <div className="join-private-match-container">
            <form onSubmit={onJoinPrivateMatch}>
                <FormInput title="Name" onChange={e => setName(e.target.value)} value={name} required />
                <FormInput title="Password" onChange={e => setPassword(e.target.value)} value={password} required />
                <FormButton title="Join private match" />
            </form>
        </div>
    );
};

export default JoinPrivateMatch;