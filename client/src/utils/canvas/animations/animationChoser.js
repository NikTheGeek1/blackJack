import DealingCardsAnimation from './DealingCards';
import PlacingTokensAnimation from './PlacingToken';
import DealingCardAnimation from './DealingCard';
import PlayerChoiceType from '../../../models/matches/PlayerChoiceType';
import MessagesManager from '../draw_messages/MessagesManager';
import RoundEndAnimation from './RoundEndAnimation';

const animationChoser = (playerChoice, canvasManager, setters) => {
    if (canvasManager.game.verdictOut) {
        MessagesManager.anotherMessageIsDisplayed = true;
        const onRoundEnd = new RoundEndAnimation(canvasManager, setters.setAnimationPlaying.bind(this, false));
        return onRoundEnd.start();
    } 
    if (playerChoice.playerChoiceType === PlayerChoiceType.STARTED_GAME) {
        const onFinishStartedGameAnimationCb = () => {
            setters.setIsInitialAnimationOver(true);
            setters.setAnimationPlaying(false);
        };
        const quitAnimationCb = () => {
            setters.setIsInitialAnimationOver(true);
            setters.setAnimationPlaying(false);
        };
        const placingTokensAnimation = new PlacingTokensAnimation(canvasManager, onFinishStartedGameAnimationCb, quitAnimationCb);
        const onDealingAnimationFinish = () => placingTokensAnimation.start();
        const dealingCardsAnimation = new DealingCardsAnimation(canvasManager, onDealingAnimationFinish, quitAnimationCb);
        dealingCardsAnimation.start();
    } else if (playerChoice.playerChoiceType === PlayerChoiceType.DREW) {
        const dealingCardAnimation = new DealingCardAnimation(canvasManager, playerChoice.playerEmail, setters.setAnimationPlaying.bind(this, false));
        dealingCardAnimation.start();
    } else if (playerChoice.playerChoiceType === PlayerChoiceType.BLACKJACKED) {
        MessagesManager.anotherMessageIsDisplayed = true;
        const dealingCardAnimation = new DealingCardAnimation(
            canvasManager,
            playerChoice.playerEmail,
            MessagesManager.drawBJ.bind(this, canvasManager, setters.setAnimationPlaying.bind(this, false))
        );
        dealingCardAnimation.start();
    } else if (playerChoice.playerChoiceType === PlayerChoiceType.BUSTED) {
        MessagesManager.anotherMessageIsDisplayed = true;
        const dealingCardAnimation = new DealingCardAnimation(
            canvasManager,
            playerChoice.playerEmail,
            MessagesManager.drawBusted.bind(this, canvasManager, setters.setAnimationPlaying.bind(this, false))
        );
        dealingCardAnimation.start();
    } else if (playerChoice.playerChoiceType === PlayerChoiceType.BET) {
        // no animation for betting
        setters.setAnimationPlaying(false);
    }
    else if (playerChoice.playerChoiceType === PlayerChoiceType.LEFT_GAME) {
        // no animation for leaving game
        setters.setAnimationPlaying(false);
    } else if (playerChoice.playerChoiceType === PlayerChoiceType.STUCK) {
        setters.setAnimationPlaying(false);
    }

};

export default animationChoser;