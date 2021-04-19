// import SocialLogin from 'react-social-login';

const SocialButton = ({mediaType, triggerLogin, ...props }) => {

    return (
        <div className="social" onClick={triggerLogin} {...props}><i className={"fa fa-"+mediaType} ></i></div>
    );
};

export default SocialButton;