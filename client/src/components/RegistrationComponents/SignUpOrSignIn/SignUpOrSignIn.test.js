import ReactDOM from 'react-dom';
import { act } from '@testing-library/react';
import SignUpOrSignIn from './SignUpOrSignIn';


let container;

beforeEach(() => {
    container = document.createElement('div');
    document.body.appendChild(container);
});

afterEach(() => {
    document.body.removeChild(container);
    container = null;
});


test('it renders a button saying sign in but when it is clicked it says sign up' , () => {
    act(() => {
        ReactDOM.render(<SignUpOrSignIn />, container);
    });
    const switchButton = container.querySelector('button');
    expect(switchButton.textContent).toBe('or SIGN_IN');

    // Test click on button
    act(() => {
        switchButton.dispatchEvent(new MouseEvent('click', {bubbles: true}));
    });
    expect(switchButton.textContent).toBe('or SIGN_UP');
});

