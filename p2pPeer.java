import java.io.*;
import java.net.*;
import java.util.*;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;


public class p2pPeer 
{

	public static void main(String[] args) throws IOException 
	{
		if (args.length != 3) 
		{
			System.out.println("Uso: java p2pPeer <server> <localport>");
			System.out.println("create nickname");
			System.out.println("list nickname");
			System.out.println("wait");
			return;
		} 
		int result = 0;

		String remoteHostName = args[0];
		String connectLocation = "rmi://" + remoteHostName + ":52369/server_if";

		p2pServerInterface serverIf = null;
		try 
		{
			System.out.println("Connecting to server at : " + connectLocation);
			serverIf = (p2pServerInterface) Naming.lookup(connectLocation);
		} 
		catch (Exception e) 
		{
			System.out.println ("Client failed: ");
			e.printStackTrace();
		}

		new p2pPeerThread(args, serverIf).start();
		new p2pPeerHeartbeat(args, serverIf).start();
		new p2pPeerClient(args, serverIf).start();
	}
}
