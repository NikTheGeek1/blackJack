import { useStore } from '../../hooks-store/store';
import './ProfilePage.css';
import { FormButton, FormInput } from '../../components/Form/components';
import { useState } from 'react';
import { addMoney } from '../../services/user/add-money';
import { changeName } from '../../services/user/change-name';
import { changePassword } from '../../services/user/change-password';
import { changeEmail } from '../../services/user/change-email';
import { } from '../../services/user/change-email';
import { UPDATE_USER } from '../../hooks-store/stores/user-credential-store';

const ProfilePage = () => {

    const [globalState, dispatch] = useStore();
    const [money, setMoney] = useState('');
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const user = globalState.userState.userObj;

    const onAddMoney = e => {
        e.preventDefault();
        if (money <= 0 || money > 1000 || !!(money % 1)) return;
        addMoney(user.email, money,
            updatedUser => dispatch(UPDATE_USER, updatedUser),
            succErr => setErrorMessage(succErr.errorMessage)
        );
    };

    const onChangeName = e => {
        e.preventDefault();
        if (!name.trim()) return;
        changeName(user.email, name,
            updatedUser => dispatch(UPDATE_USER, updatedUser),
            succErr => setErrorMessage(succErr.errorMessage)
        );
    };

    const onChangeEmail = e => {
        e.preventDefault();
        if (!email.trim() || email === user.email) return;
        changeEmail(user.id, email,
            updatedUser => dispatch(UPDATE_USER, updatedUser),
            succErr => setErrorMessage(succErr.errorMessage)
        );
    };

    const onChangePassword = e => {
        e.preventDefault();
        if (!oldPassword.trim() || !newPassword.trim()) return;
        changePassword(user.email, oldPassword, newPassword,
            updatedUser => dispatch(UPDATE_USER, updatedUser),
            succErr => setErrorMessage(succErr.errorMessage)
        );
    };

    return (
        <><div>
            <div>Name: {user.name}</div>
            <div>Email: {user.email}</div>
            <div>Money: {user.money}</div>
        </div>
            {errorMessage && <p>{errorMessage}</p>}
            <form onSubmit={onAddMoney}>
                <FormInput title="Add money: " type="number" otherProps={{ min: 1, step: 1, max: 1000 }} value={money} onChange={e => setMoney(e.target.value)} />
                <FormButton title="Add" />
            </form>
            <form onSubmit={onChangeName}>
                <FormInput title="Change name: " value={name} onChange={e => setName(e.target.value)} />
                <FormButton title="Change" />
            </form>
            <form onSubmit={onChangeEmail}>
                <FormInput title="Change email: " value={email} onChange={e => setEmail(e.target.value)} />
                <FormButton title="Change" />
            </form>
            <form onSubmit={onChangePassword}>
                <FormInput title="Old password: " value={oldPassword} onChange={e => setOldPassword(e.target.value)} />
                <FormInput title="Change password: " value={newPassword} onChange={e => setNewPassword(e.target.value)} />
                <FormButton title="Change" />
            </form>
        </>
    );
};

export default ProfilePage;