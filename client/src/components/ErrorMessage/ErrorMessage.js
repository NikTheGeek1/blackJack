import './ErrorMessage.css';

const ErrorMessage = ({ message }) => {

    return (
        <span className="error-message">{message}</span>
    );
};

export default ErrorMessage;