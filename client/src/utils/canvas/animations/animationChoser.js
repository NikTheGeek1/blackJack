import DealingCardsAnimation from './DealingCards';
import PlacingTokensAnimation from './PlacingToken';
import DealingCardAnimation from './DealingCard';
import PlayerChoiceType from '../../../models/matches/PlayerChoiceType';
import MessagesManager from '../draw_messages/MessagesManager';
import RoundEndAnimation from './RoundEndAnimation';
import VerdictSlidingTokenUtils from './VerdictSlidingTokenUtils';

const animationChoser = (playerChoice, canvasManager, setters) => {
    if (canvasManager.game.verdictOut) {
        // when the verdict is out, we need to first animate dealing a card 
        // if the player busted (and so they drew). 
        // But when the player sticks then we don't need it
        MessagesManager.anotherMessageIsDisplayed = true;
        const onRoundEnd = new RoundEndAnimation(canvasManager, setters.setAnimationPlaying.bind(this, false));
        if (playerChoice?.playerChoiceType === PlayerChoiceType.BUSTED) {
            const onDealingAnimationFinish = () => onRoundEnd.start();
            const dealingCardAnimation = new DealingCardAnimation(canvasManager, playerChoice.playerEmail, onDealingAnimationFinish);
            return dealingCardAnimation.start();
        }
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
        VerdictSlidingTokenUtils.setInstance(canvasManager); //  When they either BJ or finish betting is the appropriate time  to initialise that singleton
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
        VerdictSlidingTokenUtils.setInstance(canvasManager); //  When they either BJ or finish betting is the appropriate time  to initialise that singleton
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