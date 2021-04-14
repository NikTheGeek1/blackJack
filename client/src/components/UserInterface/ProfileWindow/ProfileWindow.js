import './ProfileWindow.css';
import { FormButton } from '../../Form/components';
import { useHistory } from 'react-router';
// make this like facebook top right cornet where you click
// on your name and this small window comes up and lets
// you visit different things like your profile page etc
const ProfileWindow = () => {
    const history = useHistory();

    const onSubmitForm = () => {
        history.push('/profile');
    };

    return (
        <form onSubmit={onSubmitForm}>
            <FormButton title="Profile"/>
        </form>
    );
};

export default ProfileWindow;