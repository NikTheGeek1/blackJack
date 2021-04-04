import DealingCardsAnimation from './DealingCards';
import PlacingTokensAnimation from './PlacingToken';
import DealingCardAnimation from './DealingCard';
import PlayerChoiceType from '../../../models/matches/PlayerChoiceType';
import { UNSET_PLAYER_CHOICE } from '../../../hooks-store/stores/player-choice-store';

const animationChoser = (playerChoice, canvasManager, dispatch, setters) => {

    switch (playerChoice.playerChoiceType) {
        case PlayerChoiceType.STARTED_GAME:
            const onFinishCb = () => {
                setters.setIsInitialAnimationOver(true);
                setters.setAnimationPlaying(false);
            };
            const placingTokensAnimation = new PlacingTokensAnimation(canvasManager, onFinishCb);
            const onDealingAnimationFinish = () => placingTokensAnimation.start();
            const dealingCardsAnimation = new DealingCardsAnimation(canvasManager, onDealingAnimationFinish);
            dealingCardsAnimation.start();
            break;
        case PlayerChoiceType.DREW:
            const dealingCardAnimation = new DealingCardAnimation(canvasManager, playerChoice.playerEmail, setters.setAnimationPlaying.bind(this, false));
            dealingCardAnimation.start();
            break
        default:
            break;
    }
    dispatch(UNSET_PLAYER_CHOICE);

};

export default animationChoser;