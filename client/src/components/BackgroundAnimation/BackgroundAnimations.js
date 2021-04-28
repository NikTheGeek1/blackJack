import './BackgroundAnimation.css';

const BackgroundAnimation = ({ zIndex }) => {

    return (
        <ul className="circles" style={{ zIndex: zIndex ? zIndex : -1 }}>
            <li></li>
            <li></li>
            <li></li>
            <li></li>
            <li></li>
            <li></li>
            <li></li>
            <li></li>
            <li></li>
            <li></li>
        </ul>
    );
};

export default BackgroundAnimation;