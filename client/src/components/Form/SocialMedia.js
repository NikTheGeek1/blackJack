import "./SocialMedia.css"
import SocialButton from './SocialButton';

const SocialMedia = () => {

    return (
        <div className="social-container">
            <SocialButton mediaType="facebook"/>
            <SocialButton mediaType="google" />
            <SocialButton mediaType="instagram" />
        </div>
    );
};

export default SocialMedia;