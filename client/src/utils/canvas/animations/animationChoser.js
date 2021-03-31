import DealingCardsAnimation from './DealingCards';
import PlacingTokensAnimation from './PlacingToken';
import PlayerChoiceType from '../../../models/matches/PlayerChoiceType';
import { UNSET_PLAYER_CHOICE } from '../../../hooks-store/stores/player-choice-store';

const animationChoser = (playerChoice, canvasManager, dispatch, setIsInitialAnimationOver) => {

    switch (playerChoice.playerChoiceType) {
        case PlayerChoiceType.STARTED_GAME:
            const placingTokensAnimation = new PlacingTokensAnimation(canvasManager, setIsInitialAnimationOver.bind(this, true));
            const onDealingAnimationFinish = () => placingTokensAnimation.start();
            const dealingCardsAnimation = new DealingCardsAnimation(canvasManager, onDealingAnimationFinish);
            dealingCardsAnimation.start();
            // const placingTokensAnimation = new PlacingTokensAnimation(canvasManager, () => {});
            // placingTokensAnimation.start();
            break;
        default:
            break;
    }
    dispatch(UNSET_PLAYER_CHOICE);

};

export default animationChoser;