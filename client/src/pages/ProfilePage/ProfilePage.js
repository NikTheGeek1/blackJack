import { useStore } from '../../hooks-store/store';
import './ProfilePage.css';
import {FormButton, FormInput} from '../../components/Form/components';
import { useState } from 'react';
import { addMoney } from '../../services/user/add-money';

const ProfilePage = () => {

    const [globalState, dispatch] = useStore();
    const [money, setMoney] = useState('');
    const user = globalState.userState.userObj;

    const onAddMoney = e => {
        e.preventDefault();
        if (money <= 0 || money > 1000 || !!(money % 1)) return;
        addMoney(user.email, money,
            succRes => console.log(succRes), // TODO: dispatch updated user to global store
            succErr => console.log(succErr)
            );
    };

    return (
        <><div>
            <div>Name: {user.name}</div>
            <div>Email: {user.email}</div>
            <div>Money: {user.money}</div>
        </div>
        <form onSubmit={onAddMoney}>
            <FormInput title="Add money: " type="number" otherProps={{min: 1, step:1, max: 1000}} value={money} onChange={e => setMoney(e.target.value)}/>
            <FormButton title="Add"/>
        </form>
        </>
    );
};

export default ProfilePage;