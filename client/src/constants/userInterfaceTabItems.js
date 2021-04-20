import JoinGame from '../components/UserInterface/TableTabs/JoinGame/JoinGame';
import  NewMatchForm from '../components/NewMatch/NewMatch';

const tabs = {
    JOIN_GAME: { label: "Join game", component: JoinGame },
    CREATE_GAME: { label: "Create game", component: NewMatchForm},
    FIND_PRIVATE_GAME: { label: "Find private game", component: null},
    INFORMATIONS: { label: "Informations", component: null}
};

export const tabsKeys = Object.keys(tabs);
export const tabsValues = Object.values(tabs).map(v => v.label);
export default tabs;