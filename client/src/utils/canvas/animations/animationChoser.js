import DealingCardsAnimation from './DealingCards';
import PlacingTokensAnimation from './PlacingToken';
import PlayerChoiceType from '../../../models/matches/PlayerChoiceType';
const animationChoser = (playerChoice, canvasManager) => {

    switch (playerChoice.playerChoiceType) {
        case PlayerChoiceType.GAME_STARTED_DEALING:
            const placingTokensAnimation = new PlacingTokensAnimation(canvasManager, () => {});
            const onDealingAnimationFinish = () => placingTokensAnimation.start();
            const dealingCardsAnimation = new DealingCardsAnimation(canvasManager, onDealingAnimationFinish);
            dealingCardsAnimation.start();
            // const placingTokensAnimation = new PlacingTokensAnimation(canvasManager, () => {});
            // placingTokensAnimation.start();
            break;
        default:
            break;
    }

};

export default animationChoser;