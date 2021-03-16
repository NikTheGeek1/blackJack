import './Sender.css';
import Message from '../../../models/chat/Message';

import { FormInput, FormButton } from '../../Form/components'
import { useStore } from '../../../hooks-store/store';
import URLs from '../../../services/DEV-URLs';
import { useState } from 'react';

const Sender = ({ chatSocket }) => {
    const [message, setMessage] = useState('');
    const globalState = useStore(false)[0];
    const matchName = globalState.matchState.matchObj.matchName;
    const senderName = globalState.playerState.playerObj.name;
    const senderEmail = globalState.playerState.playerObj.email

    const sendMsgHandler = e => {
        e.preventDefault();
        const rawMsg = message;
        if (!rawMsg.trim()) return;
        const msg = new Message(senderName, senderEmail, rawMsg);
        chatSocket.send(URLs.SEND_MESSAGE(matchName), {}, JSON.stringify(msg));
        setMessage('');
    }


    return (
        <form className="chat-sender-container" onSubmit={e => sendMsgHandler(e)}>
            <FormInput type="text" otherProps={{ placeholder: "Aa" }} onChange={e => setMessage(e.target.value)} value={message} />
            <FormButton title="Send" />
        </form>
    );
};

export default Sender;