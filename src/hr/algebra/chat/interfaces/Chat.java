package hr.algebra.chat.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Chat extends Remote {

    void sendUserList(String[] userList) throws RemoteException;

    void receiveMessage(String message) throws RemoteException;
}
