package hr.algebra.threerp3.stratego.chat;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ChatRemoteServiceImpl implements ChatRemoteService {
    private List<String> chatMessages;

    public ChatRemoteServiceImpl() {
        chatMessages = new ArrayList<>();
    }

    @Override
    public void sendChatMessage(String chatMessage) throws RemoteException {
        chatMessages.add(chatMessage);
    }

    @Override
    public List<String> getAllChatMessages() throws RemoteException {
        return chatMessages;
    }
}
