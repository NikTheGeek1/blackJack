import { useEffect, useRef, useState } from 'react';
import './GameInterface.css';
import tableImg from '../../assets/bj-table-computer.png';
import positionImg from '../../assets/bj-player-position.png';
import CanvasManager from '../../models/canvas/CanvasManager';
import CanvasImgNames from '../../constants/CanvasImgNames';

let canvasManager;
const GameInterface = () => {
    const [screenDimensions, setScreenDimensions] = useState({ width: window.innderWidth, height: window.innerHeight });
    const canvasRef = useRef(null);

    useEffect(() => {
        window.addEventListener("resize", screenDimensionsHandler);

        return () => {
            window.removeEventListener("resize", screenDimensions);
        };
    }, []);

    const screenDimensionsHandler = () => {
        setScreenDimensions({ width: window.innerWidth, height: window.innerHeight });
    };

    const canvasStyle = { width: screenDimensions.width * .8, height: screenDimensions.height * .8 };
    useEffect(() => {
        const canvasRefCurrent = canvasRef.current;
        const imgsArray = [
            { src: tableImg, name: CanvasImgNames.TABLE, height: 600, width: 1000 },
            { src: positionImg, name: CanvasImgNames.POSITION, height: 74, width: 74 },
        ];
        canvasManager = new CanvasManager(canvasRefCurrent, { w: 1000, h: 1000 }, imgsArray);
        canvasManager.loadImagesAndStart();
    }, []);

    return (
        <canvas ref={canvasRef} className="bj-interface-canvas" height="800px" width="1200px" style={canvasStyle} />
    );
};

export default GameInterface;