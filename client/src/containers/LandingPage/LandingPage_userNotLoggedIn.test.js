import ReactDOM from 'react-dom';
import { act } from '@testing-library/react';
import LandingPage from './LandingPage';



let container;

beforeEach(() => {
    container = document.createElement('div');
    document.body.appendChild(container);
});

afterEach(() => {
    document.body.removeChild(container);
    container = null;
});


jest.mock('../../hooks-store/store', () => ({
    useStore: () => ([
        { userState: { isLoggedIn: false } },
        jest.fn(() => { })
    ])
}));

test('it renders a text (on a button) saying or SING_IN', () => {
    act(() => {
        ReactDOM.render(<LandingPage />, container);
    });
    const button = container.querySelector('button');
    expect(button.textContent).toBe('or SIGN_IN');

});

