import java.net.*;
import java.time.*;
import java.util.*;

public class p2pServerTimer extends Thread
{
    private HashMap<InetAddress, LocalDateTime> timers;
    private ArrayList<Peer> peers;
    public p2pServerTimer(HashMap<InetAddress, LocalDateTime> timers, ArrayList<Peer> peers)
    {
        this.timers = timers;
        this.peers = peers;
    }

    public void run()
    {
        while (true)
        {
            for (InetAddress peerAddress : timer.getKeys())
            {
                if (timers.get(peerAddress).isBefore(LocalDateTime.now().minusSeconds(30)))
                {
                    timers.remove(peerAddress);
                    for (Peer p : peers)
                    {
                        if (p.address.equals(peerAddress))
                        {
                            peers.remove(p);
                            break;
                        }
                    }
                }
            }

            Thread.sleep(5000);
        }
    }
}