import './App.css';
import LandingPage from './containers/LandingPage/LandingPage';
import userCredentialStore from './hooks-store/stores/user-credential-store';

userCredentialStore();

function App() {
  return (
    <LandingPage />
  );
}

export default App;
