import { useEffect, useRef, useState } from 'react';
import './GameInterface.css';
import cardImgs from '../../utils/importingCardImgs';
import tableImg from '../../assets/bj-table-computer.png';
import positionImg from '../../assets/bj-player-position.png';
import cardBackBlueImg from '../../assets/cards/card-back-blue.svg';
import CanvasManager from '../../models/canvas/CanvasManager';
import CanvasImgNames from '../../constants/CanvasImgNames';
import { useStore } from '../../hooks-store/store';
import { UNSET_PLAYER_CHOICE } from '../../hooks-store/stores/player-choice-store'
import CanvasDynamicSizesManager from '../../utils/CanvasDynamicManager';

let canvasManager;
const GameInterface = ({ screenDimensions }) => {
    const canvasRef = useRef(null);
    const [mousePos, setMousePos] = useState({ x: 0, y: 0 });
    const [mousePosOrigin, setMousePosOrigin] = useState({ x: 0, y: 0 });
    const [globalState, dispatch] = useStore();
    const match = globalState.matchState.matchObj;
    const playerChoice = globalState.playerChoiceState.playerChoiceObj;

    useEffect(() => {
        const canvasRefCurrent = canvasRef.current;
        const imgsArray = [...cardImgs,
        { src: tableImg, name: CanvasImgNames.TABLE },
        { src: positionImg, name: CanvasImgNames.POSITION },
        { src: cardBackBlueImg, name: CanvasImgNames.CARD_BACK_BLUE }
        ];
        // TODO: canvas renders anew a couple of times, put a debugger somewhere and check render cycles 
        canvasManager = new CanvasManager(canvasRefCurrent, screenDimensions, imgsArray);
        canvasManager.loadImagesAndStart(screenDimensions);
    }, []);

    useEffect(() => {
        canvasManager.drawAll(screenDimensions);
    }, [screenDimensions]);

    const clickHandler = e => {
        if (mousePosOrigin.x !== 0 && mousePosOrigin.y !== 0) {
            setMousePosOrigin({ x: 0, y: 0 });
        } else {
            setMousePosOrigin({ x: mousePos.x, y: mousePos.y });
        }
    };

    const updateMousePos = e => {
        const rect = canvasManager.canvas.getBoundingClientRect();
        // const root = document.documentElement;
        const x = Math.round((e.clientX - rect.left - mousePosOrigin.x) * (CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR / screenDimensions.width));// - root.scrollLeft;
        const y = Math.round((e.clientY - rect.top - mousePosOrigin.y) * (CanvasDynamicSizesManager.constants.SCALING_DENOMINATOR / screenDimensions.width));// - root.scrollTop;
        setMousePos({ x, y });
        canvasManager.updateMousePos(x, y);
    };

    useEffect(() => {
        canvasManager.canvas.addEventListener('click', clickHandler);
        canvasManager.canvas.addEventListener('mousemove', updateMousePos);
        return () => {
            canvasManager.canvas.removeEventListener('click', clickHandler);
            canvasManager.canvas.removeEventListener('mousemove', updateMousePos);
        };
    }, [clickHandler, updateMousePos]);

    useEffect(() => {
        const playerChoice = { playerEmail: null, playerChoiceType: "GAME_STARTED_DEALING" }
        const matchGame = {
            players: [],
            dealer: { id: 1, name: "aa", email: "aa", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "CLUBS", rank: "ACE11", visibility: "REVEALED" },], bet: 0, isDealer: true, status: "WAITING_GAME" },
            allPlayersDealerFirst: [
                { id: 1, name: "aa", email: "aa", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "CLUBS", rank: "ACE11", visibility: "REVEALED" },], bet: 0, isDealer: true, status: "WAITING_GAME" },
                { id: 2, name: "bb", email: "bb", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "DIAMONDS", rank: "THREE", visibility: "REVEALED" },], bet: 0, isDealer: false, status: "WAITING_GAME" },
                { id: 3, name: "cc", email: "cc", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "HEARTS", rank: "FOUR", visibility: "REVEALED" },], bet: 0, isDealer: false, status: "WAITING_GAME" },
                { id: 4, name: "dd", email: "dd", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "CLUBS", rank: "FIVE", visibility: "REVEALED" },], bet: 0, isDealer: false, status: "WAITING_GAME" },
                { id: 5, name: "ee", email: "ee", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "SPADES", rank: "SIX", visibility: "REVEALED" },], bet: 0, isDealer: false, status: "WAITING_GAME" },
                { id: 6, name: "ff", email: "ff", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "HEARTS", rank: "SEVEN", visibility: "REVEALED" },], bet: 0, isDealer: false, status: "WAITING_GAME" },
                { id: 7, name: "gg", email: "gg", money: 1000, displayedCards: [{ suit: null, rank: null, visibility: "HIDDEN" }, { suit: "DIAMONDS", rank: "EIGHT", visibility: "REVEALED" },], bet: 0, isDealer: false, status: "WAITING_GAME" },
            ]
        };

        if (!playerChoice) return;
        canvasManager.updateDrawing(
            matchGame, playerChoice
        );

        dispatch(UNSET_PLAYER_CHOICE);
    }, [match, playerChoice]);


    return (
        <>
            <div style={{ position: "absolute", top: 0, left: 0, zIndex: 1, color: (mousePosOrigin.x !== 0 && mousePosOrigin.y !== 0) ? "red" : "white", fontSize: 40 }}>X: {mousePos.x}, Y: {mousePos.y}</div>
            {/* style={GameInterfaceCss.canvasStyle(screenDimensions)} */}
            <canvas ref={canvasRef} className="bj-interface-canvas" height={screenDimensions.height} width={CanvasDynamicSizesManager.sizeUtils.canvasStyle(screenDimensions).width} />
            {/* <canvas ref={canvasRef} className="bj-interface-canvas" height={screenDimensions.height} width={screenDimensions.width * .76}  /> */}
        </>
    );
};

export default GameInterface;