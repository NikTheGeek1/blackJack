import './InfoMessage.css';

const InfoMessage = ({message, extraClasses}) => {

    return (
        <div className={"information-box-container " + extraClasses }>
            <div className="information-box" >!</div>
            <div className="information-message" >{message}</div>
        </div>
    );
};

export default InfoMessage;