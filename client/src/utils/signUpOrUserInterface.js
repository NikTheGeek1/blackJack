import SignUpOrIn from '../components/RegistrationComponents/SignUpOrSignIn/SignUpOrSignIn';
import UserInterface from '../pages/UserInterface/UserInterface';

const signUpORUserInterface = (isUserLoggedIn) => {
    let JSX = <SignUpOrIn />;
    
    if (isUserLoggedIn) {
        JSX = <UserInterface />;
    }
    return JSX;
};

export default signUpORUserInterface;