package hr.algebra.chat.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {

    String DEFAULT_NAME = "ChatServer";


    void addUser(String name, Chat client) throws RemoteException;

    void logOut(String name) throws RemoteException;

    void postMessage(String name, String message) throws RemoteException;

    boolean requestAvailable(int id) throws RemoteException;

    void emptyRequests() throws RemoteException;
}
