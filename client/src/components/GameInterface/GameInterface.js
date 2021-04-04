import './GameInterface.css';
import { useEffect, useRef, useState } from 'react';
import cardImgs from '../../utils/canvas/imports/cardImgs';
import tokenImgs from '../../utils/canvas/imports/tokenImgs';
import tableImg from '../../assets/bj-table-computer.png';
import positionImg from '../../assets/bj-player-position.png';
import cardBackBlueImg from '../../assets/cards/card-back-blue.svg';
import CanvasManager from '../../models/canvas/CanvasManager';
import CanvasImgNames from '../../constants/canvas/ImgNames';
import { useStore } from '../../hooks-store/store';
import animationChoser from '../../utils/canvas/animations/animationChoser';
import CanvasDynamicSizesManager from '../../utils/canvas/coordinates_sizes/DynamicManager';
import MouseLocator from '../../utils/canvas/mouse_locators/MouseLocator';
import HoverOvertTypes from '../../utils/canvas/mouse_locators/HoverOverTypes';
import BetTokenAnimation from '../../utils/canvas/animations/BetToken';
import CancelBetTokenAnimation from '../../utils/canvas/animations/CancelBetToken';
import arrowImg from '../../assets/arrow.png';

let canvasManager;
const GameInterface = ({ screenDimensions, gameSocketManager }) => {
    const canvasRef = useRef(null);
    const [mousePos, setMousePos] = useState({ x: 0, y: 0 });
    const [mousePosOrigin, setMousePosOrigin] = useState({ x: 0, y: 0 });
    const [allImgsLoaded, setAllImgsLoaded] = useState(false);
    const [isInitialAnimationOver, setIsInitialAnimationOver] = useState(false);
    const [globalState, dispatch] = useStore();
    const [animationPlaying, setAnimationPlaying] = useState(false);
    const match = globalState.matchState.matchObj;
    const thisPlayer = globalState.playerState.playerObj;
    const playerChoice = globalState.playerChoiceState.playerChoiceObj;

    useEffect(() => {
        const canvasRefCurrent = canvasRef.current;
        const imgsArray = [...cardImgs, ...tokenImgs,
        // TODO: export the importing of these images in a different file, as you've done with the cardImgs and tokenImgs
        { src: tableImg, name: CanvasImgNames.TABLE },
        { src: positionImg, name: CanvasImgNames.POSITION },
        { src: cardBackBlueImg, name: CanvasImgNames.CARD_BACK_BLUE },
        { src: arrowImg, name: CanvasImgNames.ARROW }
        ];
        // TODO: canvas renders anew couple of times, put a debugger somewhere and check render cycles 
        canvasManager = new CanvasManager(canvasRefCurrent, screenDimensions, imgsArray, thisPlayer, match.game);
        canvasManager.loadImagesAndStart(screenDimensions, setAllImgsLoaded);
    }, []);

    useEffect(() => {
        if (!allImgsLoaded) return;
        console.log(playerChoice, 'GameInterface.js', 'line: ', '121');
        if (playerChoice?.playerChoiceType) {
            // TODO: enable no clicking
            setAnimationPlaying(true);
            console.log("animationPlaying", 'GameInterface.js', 'line: ', '52');
            canvasManager.updateGame(match.game);
            canvasManager.updateThisPlayer(thisPlayer);
            animationChoser(playerChoice, canvasManager, dispatch, { setIsInitialAnimationOver, setAnimationPlaying });
        } else {
            // enable clicking
        }

    }, [thisPlayer, match, playerChoice, allImgsLoaded]);

    useEffect(() => {
        console.log(animationPlaying, 'GameInterface.js', 'line: ', '62');
        if (!allImgsLoaded || animationPlaying) return;
        canvasManager.updateGame(match.game);
        canvasManager.updateThisPlayer(thisPlayer);
        canvasManager.drawAll(isInitialAnimationOver, isInitialAnimationOver);
    }, [match, thisPlayer, animationPlaying]);

    useEffect(() => {
        if (!allImgsLoaded) return;
        canvasManager.setScreenDimensions(screenDimensions);
        canvasManager.drawAll(isInitialAnimationOver, isInitialAnimationOver);
    }, [screenDimensions]);

    const clickHandler = e => {
        if (animationPlaying) return;
        // if (mousePosOrigin.x !== 0 && mousePosOrigin.y !== 0) {
        //     setMousePosOrigin({ x: 0, y: 0 });
        // } else {
        //     setMousePosOrigin({ x: mousePos.x, y: mousePos.y });
        // }
        const mouseLocator = new MouseLocator(screenDimensions, mousePos, canvasManager.thisPlayer, match.game);
        const clickedObject = mouseLocator.analyseMouseLocation();
        if (HoverOvertTypes.TOKEN_COLUMNS.includes(clickedObject) && canvasManager.thisPlayer.tokens[clickedObject]) { // and in BETTING state
            new BetTokenAnimation(canvasManager, clickedObject).playAnimation();
        }
        if (HoverOvertTypes.BET_TOKEN_COLUMNS.includes(clickedObject) && canvasManager.thisPlayer.betTokens[clickedObject.slice(1)]) { // and in BETTING state
            new CancelBetTokenAnimation(canvasManager, clickedObject).playAnimation();
        }
        if (clickedObject === HoverOvertTypes.START_GAME_BUTTON) {
            gameSocketManager.sendStartGame();
        }
        if (clickedObject === HoverOvertTypes.BET_BUTTON) {
            gameSocketManager.sendBet(canvasManager.thisPlayer.betTokens);
        }
        if (clickedObject === HoverOvertTypes.DRAW_BUTTON) {
            gameSocketManager.sendDraw();
        }
        if (clickedObject === HoverOvertTypes.STICK_BUTTON) {
            gameSocketManager.sendStick();
        }
    };

    const updateMousePos = e => {
        const rect = canvasManager.canvas.getBoundingClientRect();
        // const root = document.documentElement;
        const x = Math.round((e.clientX - rect.left - mousePosOrigin.x) * (CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR / (screenDimensions.width < 800 ? 800 : screenDimensions.width)));// - root.scrollLeft;
        const y = Math.round((e.clientY - rect.top - mousePosOrigin.y) * (CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR / (screenDimensions.width < 800 ? 800 : screenDimensions.width)));// - root.scrollTop;
        setMousePos({ x, y });
        if (animationPlaying) return;
        const mouseLocator = new MouseLocator(screenDimensions, mousePos, canvasManager.thisPlayer, canvasManager.game);
        const mouseOnWhat = mouseLocator.analyseMouseLocation();

        if (mouseOnWhat && !canvasManager.isBackupCanvasDrawn) {
            document.getElementsByTagName("body")[0].style.cursor = "pointer";
            if (HoverOvertTypes.PLAYER_CARDS.includes(mouseOnWhat)) {
                canvasManager.drawCanvasStateToBackupCanvas();
                canvasManager.enlargeCards(mouseOnWhat);
            }
        } else if (!mouseOnWhat && canvasManager.isBackupCanvasDrawn) {
            document.getElementsByTagName("body")[0].style.cursor = "initial";
            canvasManager.drawBackupCanvasStateToCanvas(true);
        } else if (!mouseOnWhat && !canvasManager.isBackupCanvasDrawn) {
            document.getElementsByTagName("body")[0].style.cursor = "initial";
        }
    };

    useEffect(() => {
        canvasManager.canvas.addEventListener('click', clickHandler);
        canvasManager.canvas.addEventListener('mousemove', updateMousePos);
        return () => {
            canvasManager.canvas.removeEventListener('click', clickHandler);
            canvasManager.canvas.removeEventListener('mousemove', updateMousePos);
        };
    }, [clickHandler, updateMousePos]);


    return (
        <>
            <div style={{ position: "absolute", top: 0, left: 0, zIndex: 1, color: (mousePosOrigin.x !== 0 && mousePosOrigin.y !== 0) ? "red" : "white", fontSize: 40 }}>X: {mousePos.x}, Y: {mousePos.y}</div>
            <canvas ref={canvasRef} className="bj-interface-canvas" height={screenDimensions.height} width={CanvasDynamicSizesManager.sizeUtils.canvasStyle(screenDimensions).width} />
        </>
    );
};

export default GameInterface;