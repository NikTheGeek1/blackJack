import './App.css';
import LandingPage from './containers/LandingPage/LandingPage';
import userCredentialStore from './hooks-store/stores/user-credential-store';
import matchStore from './hooks-store/stores/match-store';
import playerStore from './hooks-store/stores/player-store';
import playerChoiseStore from './hooks-store/stores/player-choice-store';

userCredentialStore();
matchStore();
playerStore();
playerChoiseStore();


function App() {
  return (
    <LandingPage />
  );
}

export default App;
