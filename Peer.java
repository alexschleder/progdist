import java.net.*;
import java.util.HashMap;

public class Peer
{
    public InetAddress address;
    public int port;
    public HashMap<String, String> resources;

    public Peer()
    {
        
    }

    public Peer(InetAddress address, int port, HashMap<String, String> resources)
    {
        this.address = address;
        this.port = port;
        this.resources = resources;
    }
}