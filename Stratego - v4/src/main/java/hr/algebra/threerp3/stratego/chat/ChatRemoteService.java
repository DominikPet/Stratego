package hr.algebra.threerp3.stratego.chat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatRemoteService extends Remote {
    String REMOTE_CHAT_OBJECT_NAME = "hr.algebra.chat.service";

    void sendChatMessage(String chatMsg) throws RemoteException;

    List<String> getAllChatMessages() throws RemoteException;
}
