import java.rmi.Remote;
import java.net.*;
import java.rmi.RemoteException;
import java.util.*;

public interface p2pServerInterface extends Remote {    
    public void heartbeat(InetAddress source) throws RemoteException;
    public void registerResource(InetAddress source, int port, String resourceName, String resourceHash) throws RemoteException;
    public ArrayList<Peer> listResources(String nomeRecurso) throws RemoteException;
}
