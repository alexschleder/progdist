import java.io.*;
import java.nio.file.*;
import java.rmi.RemoteException;
import java.net.*;
import java.util.*;
import java.security.*;

public class p2pPeerThread extends Thread {
	protected DatagramSocket socket = null;
	protected InetAddress addr = null;
	protected byte[] resource = new byte[1024];
	protected byte[] response = new byte[1024];
	protected int port;
	protected String[] vars;
	p2pServerInterface serverInterface;
	String fileDirectory;

	public p2pPeerThread(String[] args, p2pServerInterface serverInterface) throws IOException 
	{
		//create <nome_do_recurso> <hash>
		addr = InetAddress.getByName(args[0]);
		port = Integer.parseInt(args[1]);
		socket = new DatagramSocket(port);
		this.serverInterface = serverInterface;
		fileDirectory = "arquivos";
	}

	public void run() 
	{
		try 
		{			
			//-=-= Criar hash para cada arquivo =-=-
			
			// Todos arquivos na pasta "arquivos"
			File file = new File(fileDirectory);
			String[] fileList = file.list();
		
			//Cria hash para cada arquivo
			HashMap<String,String> hashTable = new HashMap<>();

			for(String str : fileList) {
				hashTable.put(str, generateFileHash("arquivos\\" + str));
				// System.out.println("arq: " + str + " Hash: " + generateFileHash("arquivos\\" + str));
			}
			
			//Cria uma datagrama para cada arquivo
			for(String key : hashTable.keySet()) 
			{
				serverInterface.registerResource(addr, port, key, hashTable.get(key));
			}	
		}
			
		catch(RemoteException e)
		{
			e.printStackTrace();
		}

		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
		}
		
		while (true) 
		{
			try 
			{
				// obtem a resposta
				//Espera comunicação de outros peers
				DatagramPacket packet = new DatagramPacket(response, response.length);
				socket.setSoTimeout(500);
				socket.receive(packet);
				
				// mostra a resposta
				String data = new String(packet.getData(), 0, packet.getLength());
				System.out.println("recebido: " + data);
				//Envio do arquivo
				File file = new File(fileDirectory + "/" + data);		

				byte[] fileToSend = Files.readAllBytes(file.toPath());

				socket.send(new DatagramPacket(fileToSend, fileToSend.length, packet.getAddress(), packet.getPort()));
			}
			catch (FileNotFoundException e)
			{
				System.out.println("Requested file does not exist");
			} 

			catch (IOException e) 
			{
//				if (!vars[0].equals("wait")) {
//					// fecha o socket
//					socket.close();
//					break;
//				}
			}
		}

	}


	// https://www.baeldung.com/java-md5
	//
	// https://mkyong.com/java/java-how-to-convert-bytes-to-hex/
	// 
	public String generateFileHash(String fileName) throws NoSuchAlgorithmException, IOException 
	{			
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(Files.readAllBytes(Paths.get(fileName)));
		byte[] digest = md.digest();
		
		StringBuilder result = new StringBuilder();
        for (byte aByte : digest) {
            result.append(String.format("%02X", aByte));
        }
        return result.toString();
			
	}
}
