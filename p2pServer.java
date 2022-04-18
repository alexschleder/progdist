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
	private volatile HashMap<Peer, LocalDateTime> timers;

	public p2pServer() throws RemoteException
	{
		peers = new ArrayList<>();
		timers = new HashMap<>();
		new p2pServerTimer(timers, peers).start();
	}

	public static void main(String[] args) throws IOException 
	{
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
	
	public synchronized void heartbeat(InetAddress source, int port) 
	{
		System.out.println("heartbeat from " + source + ":" + port);
		Peer currentPeer = null;
		for (Peer p : peers)
		{
			if (p.address.equals(source) && p.port == port)
			{
				currentPeer = p;
			}
		}
		try
		{
			if (currentPeer == null)
			{
				throw (new Exception("peer not found"));
			}
			timers.replace(currentPeer, LocalDateTime.now());
		}
		catch (Exception e)
		{
			System.out.println("Source for heartbeat does not exist in registered peers");
		}
	}

	public synchronized void registerResource(InetAddress source, int port, String resourceName, String resourceHash)
	{
		System.out.println("Resource " + resourceName + " from " + source + " on port " + port + " with hash " + resourceHash);
		Peer currentPeer = null;
		for (Peer p : peers)
		{
			if (p.address.equals(source) && p.port == port)
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
			timers.put(currentPeer, LocalDateTime.now());
		}
		
		if (!currentPeer.resources.containsKey(resourceName))
		{
			currentPeer.resources.put(resourceName, resourceHash);
		}
		
		System.out.println(peers);
	}

	// Retornar lista de peers
	public synchronized ArrayList<String> listResources(String nomeRecurso)
	{
		System.out.println("Resource list request received");
		ArrayList<String> result = new ArrayList<String>();

		for(Peer p : peers) 

		{
			for(String recurso : p.resources.keySet()) 
			{
				if(nomeRecurso != null) {
					if(recurso.contains(nomeRecurso)) 
					{
						result.add(p.toString());
						break;
					}
				} 
				else
				{
					result.add(p.toString());
					break;
				}
			}
		}

		System.out.println(result);
		return result;
	}
}
