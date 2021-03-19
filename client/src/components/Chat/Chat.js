import { useEffect, useState } from 'react';
import { initSocket } from '../../websockets/web-sockets-chat-rep';
import { useStore } from '../../hooks-store/store';
import URLs from '../../services/DEV-URLs';
import './Chat.css';
import Sender from './Sender/Sender';


let chatSocket;
const Chat = ({ screenDimensions }) => {
    const globalState = useStore(false)[0];
    const matchName = globalState.matchState.matchObj.matchName;
    const [chatHistory, setChatHistory] = useState([]);

    useEffect(() => {
        chatSocket = initSocket();
    }, []);

    useEffect(() => {
        let updateChatSubscription;
        window.onbeforeunload = () => {
            leavingPageHandler([updateChatSubscription]);
        };

        chatSocket.connect({}, frame => {
            updateChatSubscription = chatSocket.subscribe(URLs.UPDATE_CHAT_HISTORY(matchName), (msgHistory) => {
                const chatHistoryParsed = JSON.parse(msgHistory.body);
                setChatHistory(chatHistoryParsed);
            });
            chatSocket.send(URLs.GET_CHAT_HISTORY(matchName), {}, 'give me chat history');

        });

        return () => {
            leavingPageHandler([updateChatSubscription]);
        };
    }, []);



    const leavingPageHandler = (subscriptions) => {
        for (const sub of subscriptions) {
            sub?.unsubscribe();
        }
        chatSocket.send(URLs.LEAVE_CHAT(matchName), {}, 'leaving chat');
        chatSocket.disconnect();
    };

    const messagesJSX = chatHistory.length && chatHistory.map((msg, idx) => {
        return (
            <div key={msg.senderEmail + idx} className="message">{msg.senderName}: {msg.message}</div>
        );
    });

    return (    
        <div className="chat-outter-container">
            <div className="chat-inner-container">
                <div className="chat-title-container">BJ Chat</div>
                <div className="chat-body-container">{messagesJSX}</div>
                <Sender chatSocket={chatSocket} />
            </div>
        </div>
    );
};

export default Chat;