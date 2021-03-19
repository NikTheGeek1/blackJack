import { getFontSize } from './fontSize';


const getMultiplier = () => {
    const fontSize = getFontSize();
    return fontSize * .625;
};

export default getMultiplier;