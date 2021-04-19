import './SignInForm.css';
import { FormButton, FormInput } from '../../../Form/components';
import { useState } from 'react';
import { fetchUserByEmailAndPassword } from '../../../../services/user/fetch-user';
import { useStore } from '../../../../hooks-store/store';
import { LOG_USER_IN } from '../../../../hooks-store/stores/user-credential-store';
import PlainText from '../../../Headings/PlainText';
import SocialMedia from '../../../Form/SocialMedia';

const SignInForm = () => {
    const dispatch = useStore()[1];
    const [errorMessage, setErrorMessage] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const submitFormHandler = e => {
        e.preventDefault();
        // TODO: VALIDATION
        fetchUserByEmailAndPassword(email, password,
            succRes => onSuccessfulResponse(succRes),
            errResp => errorHandler(errResp)
        );
    };

    const onSuccessfulResponse = response => {
        dispatch(LOG_USER_IN, response);
    };

    const errorHandler = err => {
        console.log(err, 'SignInForm.js', 'line: ', '24');
        err.errorMessage ? setErrorMessage(err.errorMessage) : setErrorMessage('Server is busy, try again later');
    };

    return (
        <form onSubmit={submitFormHandler} className="sign-in-and-up-form">
            {errorMessage && <p>{errorMessage}</p>}
                    <h1>Sign in</h1>
                    <SocialMedia />
                    <PlainText>or use your account</PlainText>
                    <FormInput title="Email" onChange={e => setEmail(e.target.value)} value={email}/>
                    <FormInput title="Password" onChange={e => setPassword(e.target.value)} value={password}/>
                    <FormButton title="Sign In"/>
        </form>
    );
};

export default SignInForm;