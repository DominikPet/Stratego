package hr.algebra.threerp3.stratego.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class RMIClient {
    public static void main(String[] args) {

        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            RemoteService stub = (RemoteService) registry.lookup(RemoteService.REMOTE_OBJECT_NAME);

            handleRemoteCalls(stub);

        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    private static void handleRemoteCalls(RemoteService remoteService) throws RemoteException {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Insert sentence: ");
            String sentence = scanner.nextLine();
            int consonants = remoteService.countConsonants(sentence);
            System.out.printf("Number of consonants: %d%n", consonants);

            System.out.print("Insert character to replace: ");
            char oldChar = scanner.next().charAt(0);
            System.out.print("Insert replacement character: ");
            char newChar = scanner.next().charAt(0);
            sentence = remoteService.swapCharacters(sentence, oldChar, newChar);
            System.out.println("Changed sentence:" + sentence);
        }
    }
}
