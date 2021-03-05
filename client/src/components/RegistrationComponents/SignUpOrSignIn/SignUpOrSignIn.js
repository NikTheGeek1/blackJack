import './SignUpOrSignIn.css';
import SignUpPage from '../SignUp/SignUpPage/SignUpPage';
import SignInPage from '../SignIn/SignInPage/SignInPage';
import PrimaryButton from '../../buttons/PrimaryButton/PrimaryButton';
import { useState } from 'react';

const pageToggle = {
    SIGN_IN: 'SIGN_UP',
    SIGN_UP: 'SIGN_IN'
};

const SignUpOrSignIn = () => {
    const [pageToggleState, setPageToggleState] = useState("SIGN_UP");

    let pageJSX = <SignUpPage />;
    if (pageToggleState === "SIGN_IN") {
        pageJSX = <SignInPage />;
    }

    return (
        <>
        <PrimaryButton title={"or " + pageToggle[pageToggleState]} onClick={() => setPageToggleState(pageToggle[pageToggleState])}/>
        {pageJSX}
        </>
    );
};

export default SignUpOrSignIn;