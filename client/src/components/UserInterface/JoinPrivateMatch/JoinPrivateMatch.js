import './JoinPrivateMatch.css';
import { FormButton, FormInput } from '../../Form/components';
import { useState } from 'react';
import ErrorMessage from '../../ErrorMessage/ErrorMessage';

const JoinPrivateMatch = ({ joinMatchHandler, errorMessage }) => {

    const [name, setName] = useState('');
    const [password, setPassword] = useState('');

    const onJoinPrivateMatch = (e) => {
        e.preventDefault();
        joinMatchHandler(name, password);
    };

    return (
        <section className="join-private-match-section">
            <form className="join-private-match-form" onSubmit={onJoinPrivateMatch}>
                <ErrorMessage message={errorMessage} />
                <FormInput title="Name" onChange={e => setName(e.target.value)} value={name} required />
                <FormInput title="Password" onChange={e => setPassword(e.target.value)} value={password} required />
                <FormButton title="Join private match" />
            </form>
        </section>
    );
};

export default JoinPrivateMatch;