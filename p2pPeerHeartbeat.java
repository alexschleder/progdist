import java.io.*;
import java.net.*;
import java.rmi.RemoteException;
import java.util.*;

public class p2pPeerHeartbeat extends Thread {
	protected DatagramSocket socket = null;
	protected DatagramPacket packet = null;
	protected InetAddress addr = null;
	protected byte[] data = new byte[1024];
	protected int porta;
	p2pServerInterface serverIf;

	public p2pPeerHeartbeat(String[] args, p2pServerInterface serverIf) throws IOException 
	{
		this.serverIf = serverIf;
		addr = InetAddress.getByName(args[0]);
	}

	public void run() 
	{
		while (true) 
		{
			try 
			{
				serverIf.heartbeat(addr);
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
