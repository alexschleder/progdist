import java.io.*;
import java.net.*;
import java.util.*;

public class p2pPeerClient extends Thread 
{
	protected DatagramSocket socket = null;
	protected DatagramPacket packet = null;
	protected InetAddress addr = null;
	protected byte[] resource = new byte[1024];
	protected byte[] response = new byte[1024];
	protected int port, peer_port;
	/*-------------------------------------*/
	protected p2pServerInterface serverInterface;

	public p2pPeerClient(String[] args, p2pServerInterface serverIf) throws IOException {
		port = Integer.parseInt(args[1]) + 101;
		socket = new DatagramSocket(port);

		serverInterface = serverIf;
	}

	private enum inputType {
		LIST, LIST_SEARCH, FILE, INVALID;
	}

	public void run() 
	{
		BufferedReader obj = new BufferedReader(new InputStreamReader(System.in));
		String str = "";	

		while (true) 
		{
			inputType curType = inputType.INVALID;

			System.out.println("\n<list/peer> <message> <ip>");
			System.out.println("Example: list user <server_ip>");
			System.out.println("Example: peer \"hello_world!\" <peer_ip> <port>");
			try 
			{
				str = obj.readLine();
				String vars[] = str.split("\\s");

				if(vars[0].equals("list")) {
					if(vars.length == 1) {
						curType = inputType.LIST;
					} else {
						curType = inputType.LIST_SEARCH;
					}
				} else if(vars[0].equals("peer")) {
					curType = inputType.FILE;
				}

				switch(curType) {
					case LIST:
						ArrayList<Peer> recursos = serverInterface.listResources(null);
						System.out.println("-=-=-=-\nResources: \n");
						for(Peer p : recursos) {
							System.out.println(p.toString());
						}
						break;
					case LIST_SEARCH:
						ArrayList<Peer> recursosSearch = serverInterface.listResources(vars[1]);
						System.out.println("-=-=-=-\nResources with term \"" + vars[1] + "\": \n");
						for(Peer p : recursosSearch) {
							System.out.println(p.toString());
						}
						break;
					case FILE:
						//comunicacao entre peers, baixar arquivo
						break;
					case INVALID:
						System.out.println("\tError: Invalid \"" + vars[0] + "\" command\n");
						break;
					default:
				}

				/*addr = InetAddress.getByName(vars[2]);
				String str2 = vars[0] + " " + vars[1];
				resource = str2.getBytes();
				if (vars.length == 4) 
				{
					System.out.println("Sending message to peer on port " + vars[3]);
					peer_port = Integer.parseInt(vars[3]);
				} 
				else 
				{
					peer_port = 9000;
				}*/
			} 
			catch (IOException e) 
			{
			}
			
			//comentar? colocar if?
			try 
			{
				packet = new DatagramPacket(resource, resource.length, addr, peer_port);
				socket.send(packet);
				
				while (true) 
				{
					try 
					{
						// obtem a resposta
						packet = new DatagramPacket(response, response.length);
						socket.setSoTimeout(500);
						socket.receive(packet);
						
						// mostra a resposta
						String resposta = new String(packet.getData(), 0, packet.getLength());
						System.out.println("recebido: " + resposta);
						//Salva o download
					} 
					catch (IOException e) 
					{
						break;
					}
				}
			} 
			catch (IOException e) 
			{
			}
		}
	}
}
