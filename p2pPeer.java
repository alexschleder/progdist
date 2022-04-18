import java.io.*;
import java.rmi.Naming;

public class p2pPeer 
{

	public static void main(String[] args) throws IOException 
	{
		if (args.length != 2) 
		{
			System.out.println("Uso: java p2pPeer <server> <localport>");
			System.out.println("create nickname");
			System.out.println("list nickname");
			System.out.println("wait");
			return;
		} 

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

		String resourceDirectory = "arquivos";
		new p2pPeerThread(args, serverIf).start();
		new p2pPeerHeartbeat(args, serverIf).start();
		new p2pPeerClient(args, serverIf, resourceDirectory).start();
	}
}
