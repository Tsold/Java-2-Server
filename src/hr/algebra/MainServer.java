package hr.algebra;

import hr.algebra.chat.interfaces.Chat;
import hr.algebra.chat.interfaces.Server;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class MainServer extends UnicastRemoteObject implements Server
{

    private Map<String, Chat> users = new HashMap<String, Chat>();

    private List<Integer> requests = new ArrayList<>();

    public MainServer() throws RemoteException
    {
        super();
    }



    //Chat

    @Override
    public synchronized void addUser(String name, Chat client)
    {
        this.users.put(name, client);
        this.sendUserListToClients();
    }

    @Override
    public synchronized void logOut(String name)
    {
        this.users.remove(name);
        this.sendUserListToClients();
    }

    public synchronized void sendUserListToClients()
    {
      refreshUserList();
    }

    private void refreshUserList()
    {
        for (Iterator<Map.Entry<String, Chat>> it = this.users.entrySet().iterator(); it.hasNext();)
        {
            try
            {
                it.next().getValue().sendUserList(this.users.keySet().toArray(new String[0]));
            } catch (RemoteException e)
            {
                it.remove();
            }
        }

    }

    @Override
    public synchronized void postMessage(String name, String message)
    {
        String finalMessage = name + ": " + message;
        boolean b = false;
        for (Iterator<Map.Entry<String, Chat>> it = this.users.entrySet().iterator(); it.hasNext();)
        {
            try
            {
                it.next().getValue().receiveMessage(finalMessage);
            } catch (RemoteException e)
            {
                it.remove();
                b = true;
            }
        }
        if (b)
        {
            this.sendUserListToClients();
        }
    }


    //Vehicle Request
    @Override
    public synchronized boolean requestAvailable(int id)
    {
        if (requests.isEmpty()){
            requests.add(id);
            return true;
        }
        return false;
    }


    @Override
    public synchronized void emptyRequests()
    {
        requests.clear();
    }



    public static void publishServer() throws RemoteException {
        Thread t = new Thread(() -> {
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
                registry.rebind(Server.DEFAULT_NAME, new MainServer());
                Thread.sleep(100);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Server online...");
        });
        t.setDaemon(true);
        t.run();


    }

    public static void main(String[] args) throws RemoteException, MalformedURLException, AlreadyBoundException,
            InterruptedException, NotBoundException
    {
        publishServer();
    }

}