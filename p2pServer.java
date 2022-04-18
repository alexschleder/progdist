import java.io.*;
import java.net.*;
import java.util.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.time.*;

public class p2pServer extends UnicastRemoteObject implements p2pServerInterface
{
	private volatile ArrayList<Peer> peers;
	private volatile HashMap<InetAddress, LocalDateTime> timers;

	public p2pServer() throws RemoteException
	{
		peers = new ArrayList<>();
		timers = new HashMap<>();
	}

	public static void main(String[] args) throws IOException 
	{
		new p2pServerTimer(timers, peers).start();
		if (args.length != 1) 
		{
			System.out.println("Usage: java Server <server ip>");
			System.exit(1);
		}

		try 
		{
			System.setProperty("java.rmi.server.hostname", args[0]);
			LocateRegistry.createRegistry(52369);
			System.out.println("java RMI registry created.");
		} 
		catch (RemoteException e) 
		{
			System.out.println("java RMI registry already exists.");
		}

		try 
		{
			String server = "rmi://" + args[0] + ":52369/server_if";
			Naming.rebind(server, new p2pServer());
			System.out.println("Server is ready.");
		} 
		catch (Exception e) 
		{
			System.out.println("Serverfailed: " + e);
		}
	}
	
	public synchronized void heartbeat(InetAddress source) 
	{
		try
		{
			lastUpdate.replace(source, LocalDateTime.now());
		}
		catch (Exception e)
		{
			System.out.println("Source for heartbeat does not exist in registered peers");
		}
	}

	public synchronized void registerResource(InetAddress source, int port, String resourceName, String resourceHash)
	{
		Peer currentPeer = null;
		for (Peer p : peers)
		{
			if (p.address.equals(source))
			{
				currentPeer = p;
			}
		}

		if (currentPeer == null)
		{
			currentPeer = new Peer();
			currentPeer.address = source;
			currentPeer.port = port;
			peers.add(currentPeer);
			lastUpdate.put(source, LocalDateTime.now());
		}
		
		if (!currentPeer.resources.containsKey(resourceName))
		{
			currentPeer.resources.put(resourceName, resourceHash);
		}
	}

	// Retornar lista de peers
	public synchronized ArrayList<Peer> listResources(String nomeRecurso)
	{
		ArrayList<Peer> result = new ArrayList<Peer>();

		for(Peer p : peers) {
			for(String recurso : p.resources.keySet()) {
				if(nomeRecurso != null) {
					if(recurso.contains(nomeRecurso)) {
						result.add(p);
						break;
					}
				} else {
					result.add(p);
					break;
				}
			}
		}

		return result;
	}
}
