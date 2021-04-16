import './SignUpOrSignIn.css';
import SignUpPage from '../SignUp/SignUpPage/SignUpPage';
import SignInPage from '../SignIn/SignInPage/SignInPage';
import PrimaryButton from '../../buttons/PrimaryButton/PrimaryButton';
import { useState } from 'react';
import backgroundImg from '../../../images/sign-up-background.jpeg';

const pageToggle = {
    SIGN_IN: 'SIGN_UP',
    SIGN_UP: 'SIGN_IN'
};

const SignUpOrSignIn = () => {
    const [pageToggleState, setPageToggleState] = useState("SIGN_UP");
    const [containerClass, setContainerClass] = useState('');

    let pageJSX = <SignUpPage />;
    if (pageToggleState === "SIGN_IN") {
        pageJSX = <SignInPage />;
    }

    const signUpButtonHandler = () => {
        setContainerClass("right-panel-active");
    };

    const signInButtonHandler = () => {
        setContainerClass('');
    };


    return (
        <div className={"container " + containerClass}>

            <div className="form-container sign-up-container">
                <form action="#">
                    <h1>Create Account</h1>
                    <div className="social-container">
                        <a href="#" className="social"><i className="fab fa-facebook-f"></i></a>
                        <a href="#" className="social"><i className="fab fa-google-plus-g"></i></a>
                        <a href="#" className="social"><i className="fab fa-linkedin-in"></i></a>
                    </div>
                    <span>or use your email for registration</span>
                    <input type="text" placeholder="Name" />
                    <input type="email" placeholder="Email" />
                    <input type="password" placeholder="Password" />
                    <button>Sign Up</button>
                </form>
            </div>

            <div className="form-container sign-in-container">
                <form action="#">
                    <h1>Sign in</h1>
                    <div className="social-container">
                        <a href="#" className="social"><i className="fab fa-facebook-f"></i></a>
                        <a href="#" className="social"><i className="fab fa-google-plus-g"></i></a>
                        <a href="#" className="social"><i className="fab fa-linkedin-in"></i></a>
                    </div>
                    <span>or use your account</span>
                    <input type="email" placeholder="Email" />
                    <input type="password" placeholder="Password" />
                    <a href="#">Forgot your password?</a>
                    <button>Sign In</button>
                </form>
            </div>

            <div className="overlay-container">
                <div className="overlay">
                    <div className="overlay-panel overlay-left">
                        <h1>We missed you!</h1>
                        <p>Enter your personal details and start playing</p>
                        <button className="ghost" onClick={signInButtonHandler} id="signIn">Sign In</button>
                    </div>
                    <div className="overlay-panel overlay-right">
                        <h1>Hello, Friend!</h1>
                        <p>Enter your personal details and start journey with us</p>
                        <button className="ghost" onClick={signUpButtonHandler}>Sign Up</button>
                    </div>
                </div>
            </div>
        </div>
    );
    // {/* <PrimaryButton title={"or " + pageToggle[pageToggleState]} onClick={() => setPageToggleState(pageToggle[pageToggleState])}/> */}
};

export default SignUpOrSignIn;