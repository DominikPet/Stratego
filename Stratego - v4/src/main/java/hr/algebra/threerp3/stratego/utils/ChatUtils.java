package hr.algebra.threerp3.stratego.utils;

import hr.algebra.threerp3.stratego.HelloApplication;
import hr.algebra.threerp3.stratego.HelloController;
import hr.algebra.threerp3.stratego.chat.ChatRemoteService;
import hr.algebra.threerp3.stratego.chat.ChatRemoteServiceImpl;
import hr.algebra.threerp3.stratego.model.ConfKey;
import hr.algebra.threerp3.stratego.model.RoleName;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ChatUtils {
    public static void startChatService() {

        Integer rmiPort = ConfigurationReader.readIntegerConfigurationValueForKey(ConfKey.RMI_PORT);
        Integer randomPortHint = ConfigurationReader.readIntegerConfigurationValueForKey(ConfKey.RANDOM_PORT_HINT);

        try {
            Registry registry = LocateRegistry.createRegistry(rmiPort);
            HelloApplication.chatRemoteService = new ChatRemoteServiceImpl();
            //chatRemoteService = new ChatRemoteServiceImpl();
            ChatRemoteService skeleton = (ChatRemoteService) UnicastRemoteObject.exportObject(HelloApplication.chatRemoteService, randomPortHint);
            registry.rebind(ChatRemoteService.REMOTE_CHAT_OBJECT_NAME, skeleton);
            System.err.println("Object registered in RMI registry");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void startChatClient() {

        Integer rmiPort = ConfigurationReader.readIntegerConfigurationValueForKey(ConfKey.RMI_PORT);

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", rmiPort);
            HelloApplication.chatRemoteService = (ChatRemoteService) registry.lookup(ChatRemoteService.REMOTE_CHAT_OBJECT_NAME);

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    public static void sendChatMessage() {
        if (HelloApplication.loggedInRoleName.equals(RoleName.SINGLE_PLAYER)) return;
        String chatMsg = HelloController.staticController.chatMsgTextField.getText();
        HelloController.staticController.chatMsgTextField.clear();
        try {
            HelloApplication.chatRemoteService.sendChatMessage(HelloApplication.loggedInRoleName.name() + ": " + chatMsg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void refreshChatMessages() {
        try {
            List<String> chatMessages = HelloApplication.chatRemoteService.getAllChatMessages();
            HelloController.staticController.chatTextArea.clear();

            for (String message : chatMessages) {
                HelloController.staticController.chatTextArea.appendText(message + "\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
