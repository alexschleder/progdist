import java.io.*;
import java.nio.file.*;
import java.net.*;
import java.util.*;
import java.security.*;

public class p2pPeerThread extends Thread {
	protected DatagramSocket socket = null;
	protected DatagramPacket packet = null;
	protected InetAddress addr = null;
	protected byte[] resource = new byte[1024];
	protected byte[] response = new byte[1024];
	protected int port;
	protected String[] vars;

	public p2pPeerThread(String[] args) throws IOException {
		//create <nome_do_recurso> <hash>
		resource = ("create " + args[1]).getBytes();
		addr = InetAddress.getByName(args[0]);
		port = Integer.parseInt(args[2]);
		// cria um socket datagrama
		socket = new DatagramSocket(port);
		vars = args[1].split("\\s");
	}

	public void run() 
	{
		try 
		{
			// envia um packet
			//Comunica o recurso para o servidor na porta 9k
			DatagramPacket packet = new DatagramPacket(resource, resource.length, addr, 9000);
			socket.send(packet);
			
			//-=-= Criar hash para cada arquivo =-=-
			
			// Todos arquivos na pasta "arquivos"
			File file = new File("arquivos");
			String[] fileList = file.list();
		
			//Cria hash para cada arquivo
			HashMap<String,String> hashTable = new HashMap<>();

			for(String str : fileList) {
				hashTable.put(str, generateFileHash("arquivos\\" + str));
				// System.out.println("arq: " + str + " Hash: " + generateFileHash("arquivos\\" + str));
			}

			
			//Cria uma datagrama para cada arquivo
			//Envia datagrama
		} 
		catch (IOException e) 
		{
			socket.close();
		} catch (NoSuchAlgorithmException e) {
			socket.close();
		}
		
		while (true) 
		{
			try 
			{
				// obtem a resposta
				//Espera comunicação de outros peers
				packet = new DatagramPacket(response, response.length);
				socket.setSoTimeout(1000);
				socket.receive(packet);
				
				// mostra a resposta
				String data = new String(packet.getData(), 0, packet.getLength());
				System.out.println("recebido: " + data);
				//Download do torrent
				
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
	public String generateFileHash(String fileName) throws NoSuchAlgorithmException, IOException {
		//String filename = "src/test/resources/test_md5.txt";
		//String checksum = "5EB63BBBE01EEED093CB22BB8F5ACDC3";
			
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(Files.readAllBytes(Paths.get(fileName)));
		byte[] digest = md.digest();
		
		StringBuilder result = new StringBuilder();
        for (byte aByte : digest) {
            result.append(String.format("%02X", aByte));
        }
        return result.toString();
			
		//assertThat(result.equals(checksum)).isTrue();
	}
}
