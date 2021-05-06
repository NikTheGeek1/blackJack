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
import PrimaryLink from '../../components/PrimaryLink/PrimaryLink';

const formTypes = {
    ADD_MONEY: "ADD_MONEY",
    CHANGE_NAME: "CHANGE_NAME",
    CHANGE_EMAIL: "CHANGE_EMAIL",
    CHANGE_PASSWORD: "CHANGE_PASSWORD"
};

const ProfilePage = () => {

    const [globalState, dispatch] = useStore();
    const [money, setMoney] = useState('');
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [formType, setFormType] = useState('');
    const user = globalState.userState.userObj;

    const onAddMoney = e => {
        e.preventDefault();
        if (money <= 0 || money > 1000 || !!(money % 1)) return;
        addMoney(user.email, money,
            updatedUser => dispatch(UPDATE_USER, updatedUser),
            succErr => setErrorMessage(succErr.errorMessage)
        );
        setErrorMessage('');
        setMoney('');
    };

    const onChangeName = e => {
        e.preventDefault();
        if (!name.trim()) return;
        changeName(user.email, name,
            updatedUser => dispatch(UPDATE_USER, updatedUser),
            succErr => setErrorMessage(succErr.errorMessage)
        );
        setErrorMessage('');
        setName('');
    };

    const onChangeEmail = e => {
        e.preventDefault();
        if (!email.trim() || email === user.email) return;
        changeEmail(user.id, email,
            updatedUser => dispatch(UPDATE_USER, updatedUser),
            succErr => setErrorMessage(succErr.errorMessage)
        );
        setErrorMessage('');
        setEmail('');
    };

    const onChangePassword = e => {
        e.preventDefault();
        if (!oldPassword.trim() || !newPassword.trim()) return;
        changePassword(user.email, oldPassword, newPassword,
            updatedUser => dispatch(UPDATE_USER, updatedUser),
            succErr => setErrorMessage(succErr.errorMessage)
        );
        setErrorMessage('');
        setNewPassword('');
        setOldPassword('');
    };

    let formJSX;
    switch (formType) {
        case formTypes.ADD_MONEY:
            formJSX = (
                <form className="profile-form" onSubmit={onAddMoney}>
                    {errorMessage && <p>{errorMessage}</p>}
                    <FormInput title="Add money: " type="number" otherProps={{ min: 1, step: 1, max: 1000 }} value={money} onChange={e => setMoney(e.target.value)} />
                    <FormButton title="Add" />
                </form>
            );
            break;
        case formTypes.CHANGE_NAME:
            formJSX = (
                <form className="profile-form" onSubmit={onChangeName}>
                    {errorMessage && <p>{errorMessage}</p>}
                    <FormInput title="Change name: " value={name} onChange={e => setName(e.target.value)} />
                    <FormButton title="Change" />
                </form>
            );
            break;
        case formTypes.CHANGE_EMAIL:
            formJSX = (
                <form className="profile-form" onSubmit={onChangeEmail}>
                    {errorMessage && <p>{errorMessage}</p>}
                    <FormInput title="Change email: " value={email} onChange={e => setEmail(e.target.value)} />
                    <FormButton title="Change" />
                </form>
            );
            break;
        case formTypes.CHANGE_PASSWORD:
            formJSX = (
                <form className="profile-form" onSubmit={onChangePassword}>
                    {errorMessage && <p>{errorMessage}</p>}
                    <FormInput title="Old password: " value={oldPassword} onChange={e => setOldPassword(e.target.value)} />
                    <FormInput title="Change password: " value={newPassword} onChange={e => setNewPassword(e.target.value)} />
                    <FormButton title="Change" />
                </form>
            );
            break;

        default:
            formJSX = null;
            break;
    }

    return (
        <section className="profile-page-section">
            <div className="profile-page-user-details-container">
                <div>
                    <span className="profile-page-user-details-title" > Name: </span> 
                    <span className="profile-page-user-details-value">{user.name}</span>
                </div>
                <div>
                    <span className="profile-page-user-details-title" > Email: </span>
                     <span className="profile-page-user-details-value">{user.email}</span>
                </div>
                <div>
                    <span className="profile-page-user-details-title" > Money: </span> 
                    <span className="profile-page-user-details-value">{user.money}</span>
                </div>
            </div>
            <div className="profile-page-change-user-details-buttons">
                <PrimaryLink onClick={() => setFormType(formTypes.ADD_MONEY)} title="Add money" />
                <PrimaryLink onClick={() => setFormType(formTypes.CHANGE_NAME)} title="Change name" />
                <PrimaryLink onClick={() => setFormType(formTypes.CHANGE_EMAIL)} title="Change email" />
                <PrimaryLink onClick={() => setFormType(formTypes.CHANGE_PASSWORD)} title="Change password" />
            </div>
            {formJSX}
        </section>
    );
};

export default ProfilePage;