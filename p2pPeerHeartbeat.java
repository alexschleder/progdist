import java.io.*;
import java.net.*;
import java.rmi.RemoteException;
import java.util.*;

public class p2pPeerHeartbeat extends Thread {
	protected DatagramSocket socket = null;
	protected DatagramPacket packet = null;
	protected InetAddress localAddress = null;
	protected byte[] data = new byte[1024];
	protected int port;
	p2pServerInterface serverIf;

	public p2pPeerHeartbeat(InetAddress localAddress, int port, p2pServerInterface serverIf) throws IOException 
	{
		this.serverIf = serverIf;
		this.localAddress = localAddress;
		this.port = port;
	}

	public void run() 
	{
		while (true) 
		{
			try 
			{
				serverIf.heartbeat(localAddress, port);
				Thread.sleep(5000);
			} 
			catch(InterruptedException e) 
			{
				//do nothing
			}
			catch (RemoteException e)
			{
				e.printStackTrace();
			}
		}
	}
}
