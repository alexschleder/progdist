import java.io.*;
import java.net.InetAddress;
import java.rmi.Naming;

public class p2pPeer 
{

	public static void main(String[] args) throws IOException 
	{
		if (args.length != 3) 
		{
			System.out.println("Uso: java p2pPeer <server_address> <local_address> <localport>");
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
		InetAddress localAddress = InetAddress.getByName(args[1]);
		int port = Integer.parseInt(args[2]);

		new p2pPeerThread(localAddress, port, serverIf).start();
		new p2pPeerHeartbeat(localAddress, port, serverIf).start();
		new p2pPeerClient(localAddress, port, serverIf, resourceDirectory).start();
	}
}
