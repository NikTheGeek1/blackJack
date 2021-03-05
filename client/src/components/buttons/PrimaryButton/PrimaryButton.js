import './PrimaryButton.css';

const PrimaryButton = ({ title, onClick }) => {

    return (
        <button onClick={onClick}>{title}</button>
    );
};

export default PrimaryButton;