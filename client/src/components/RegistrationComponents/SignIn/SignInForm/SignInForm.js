import './SignInForm.css';
import { FormButton, FormInput } from '../../../Form/components';
import { useState } from 'react';
import { fetchUserByEmailAndPassword } from '../../../../services/user/fetch-user';
import { useStore } from '../../../../hooks-store/store';
import { LOG_USER_IN } from '../../../../hooks-store/stores/user-credential-store';

const SignInForm = () => {
    const dispatch = useStore()[1];
    const [errorMessage, setErrorMessage] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const submitFormHandler = e => {
        e.preventDefault();
        // TODO: VALIDATION
        fetchUserByEmailAndPassword(email, password,
            succRes => dispatch(LOG_USER_IN, succRes),
            errResp => errorHandler(errResp)
        );
    };

    const errorHandler = err => {
        console.log(err, 'SignInForm.js', 'line: ', '24');
        err.errorMessage ? setErrorMessage(err.errorMessage) : setErrorMessage('Server is busy, try again later');
    };

    return (
        <form onSubmit={submitFormHandler}>
            {errorMessage && <p>{errorMessage}</p>}
            <FormInput title="Email" value={email} onChange={e => setEmail(e.target.value)} />
            <FormInput title="Password" value={password} onChange={e => setPassword(e.target.value)} />
            <FormButton title="Submit" />
        </form>
    );
};

export default SignInForm;