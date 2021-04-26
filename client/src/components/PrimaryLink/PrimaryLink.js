import './PrimaryLink.css';

const PrimaryLink = ({title, onClick}) => {

    return (
        <a className="primary-link" onClick={onClick}>{title}</a>
    );
};

export default PrimaryLink;