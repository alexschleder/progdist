import java.io.*;
import java.net.*;
import java.util.*;

public class p2pPeerHeartbeat extends Thread {
	protected DatagramSocket socket = null;
	protected DatagramPacket packet = null;
	protected InetAddress addr = null;
	protected byte[] data = new byte[1024];
	protected int porta;

	public p2pPeerHeartbeat(String[] args) throws IOException 
	{
		String vars[] = args;
		data = ("heartbeat " + args[0]).getBytes();
		addr = InetAddress.getByName(args[0]);
		porta = Integer.parseInt(args[2]) + 100;
		// cria um socket datagrama
		socket = new DatagramSocket(porta);
	}

	public void run() 
	{
		while (true) 
		{
			try 
			{
				packet = new DatagramPacket(data, data.length, addr, 9000);
				socket.send(packet);
			} 
			catch (IOException e) 
			{
				socket.close();
			}
			
			try 
			{
				Thread.sleep(5000);
			} 
			catch(InterruptedException e) 
			{
				//do nothing
			}
		}
	}
}
