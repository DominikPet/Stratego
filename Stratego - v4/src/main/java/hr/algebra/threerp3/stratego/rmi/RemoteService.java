package hr.algebra.threerp3.stratego.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteService extends Remote { // again marker interface, like Serializable
    String REMOTE_OBJECT_NAME = "hr.algebra.rmi.service";

    int countConsonants(String sentence) throws RemoteException;

    String swapCharacters(String sentence, char oldChar, char newChar) throws RemoteException;
}

