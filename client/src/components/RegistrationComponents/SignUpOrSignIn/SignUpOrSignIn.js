import './SignUpOrSignIn.css';
import SignUpPage from '../SignUp/SignUpPage/SignUpPage';
import SignInPage from '../SignIn/SignInPage/SignInPage';
import PrimaryButton from '../../buttons/PrimaryButton/PrimaryButton';
import { useState } from 'react';
import { Button } from '../../Form/components';
import SignInForm from '../SignIn/SignInForm/SignInForm';
import SignUpForm from '../SignUp/SignUpForm/SignUpForm';
import SmallHeading from '../../Headings/SmallHeading';
import PlainText from '../../Headings/PlainText';
import BackgroundAnimation from '../../BackgroundAnimation/BackgroundAnimations';

const SignUpOrSignIn = () => {
    const [containerClass, setContainerClass] = useState('');

    const signUpButtonHandler = () => {
        setContainerClass("right-panel-active");
    };

    const signInButtonHandler = () => {
        setContainerClass('');
    };

    return (
        <section className="susi-section">
            <BackgroundAnimation />
            <div className={"susi-inner-container " + containerClass}>
                <div className="form-container sign-up-container">
                    <SignUpForm />
                </div>

                <div className="form-container sign-in-container">
                    <SignInForm />
                </div>

                <div className="overlay-container">
                    <div className="overlay">
                        <div className="overlay-panel overlay-left">
                            <SmallHeading>One of us?</SmallHeading>
                            <PlainText>Enter your personal details<br/>and start playing</PlainText>
                            <Button title="Sign In" extraClasses="ghost-button" onClick={signInButtonHandler} />
                        </div>
                        <div className="overlay-panel overlay-right">
                            <SmallHeading>New here?</SmallHeading>
                            <PlainText>Enter your personal details and<br/>start journey with us</PlainText>
                            <Button title="Sign Up" extraClasses="ghost-button" onClick={signUpButtonHandler} />
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
};

export default SignUpOrSignIn;