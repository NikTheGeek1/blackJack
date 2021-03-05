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
        { userState: { isLoggedIn: true } },
        jest.fn(() => { })
    ])
}));

test('it renders a paragraph saying that it is the user interface', () => {
    act(() => {
        ReactDOM.render(<LandingPage />, container);
    });
    const p = container.querySelector('p');
    expect(p.textContent).toBe('this is the user interface page');
});

