import './SignUpForm.css';
import { useState } from 'react';
import { createUser } from '../../../../services/user/create-user';
import { useStore } from '../../../../hooks-store/store';
import { LOG_USER_IN } from '../../../../hooks-store/stores/user-credential-store';
import { FormButton, FormInput } from '../../../Form/components';
import PlainText from '../../../Headings/PlainText';
import SocialMedia from '../../../Form/SocialMedia';

const SignUpForm = () => {
    const [errorMessage, setErrorMessage] = useState("");
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const dispatch = useStore()[1];

    const submitFormHandler = e => {
        e.preventDefault();
        // TODO: VALIDATION
        const userObj = { name, email, password };
        createUser(userObj,
            succResp => dispatch(LOG_USER_IN, succResp),
            errResp => errorHandler(errResp)
        );
    };
    const errorHandler = err => {
        console.log(err, 'SignUpForm.js', 'line: ', '25');
        err.errorMessage ? setErrorMessage(err.errorMessage) : setErrorMessage('Server is busy, try again later');
    };

    return (
        <form onSubmit={submitFormHandler} className="sign-in-and-up-form">
            {errorMessage && <p>{errorMessage}</p>}
            <h1>Create Account</h1>
            <SocialMedia />
            <PlainText>or use your email for registration</PlainText>
            <FormInput title="Name" onChange={e => setName(e.target.value)} value={name} />
            <FormInput title="Email" onChange={e => setEmail(e.target.value)} value={email} />
            <FormInput title="Password" onChange={e => setPassword(e.target.value)} value={password} type="password" />
            <FormButton title="Sign Up" />
        </form>
    );
};


export default SignUpForm;
