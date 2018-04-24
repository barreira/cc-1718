import com.sun.management.OperatingSystemMXBean;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

public class AgenteUDP extends Thread {

    private MulticastSocket socket;
    private byte[] buffer;
    private OperatingSystemMXBean os;

    private static final int PROBING_INTERVAL = 10000;
    private static final String MULTICAST_ADDR = "239.8.8.8";
    private static final int MULTICAST_PORT = 8888;
    private static final int BUFFER_SIZE = 1024;
    private static final String PROBING_MSG = "Probing servers...";
    private static final int NUM_SERVERS = 3;

    public AgenteUDP() {
        socket = null;
        buffer = null;
        os = null;
    }

    public void run() {
        try {
            socket = new MulticastSocket(MULTICAST_PORT);

            InetAddress group = InetAddress.getByName(MULTICAST_ADDR);
            socket.joinGroup(group);

            while (true) {
                buffer = new byte[BUFFER_SIZE];
                DatagramPacket message = new DatagramPacket(buffer, buffer.length);
                socket.receive(message);

                byte cpuLoad = (byte) (os.getSystemCpuLoad() * 100);
                int freeRAM = (int) (os.getFreePhysicalMemorySize() >> 20); // B -> MB

                Random rand = new Random();
                int time = rand.nextInt(3000) + 1;

                Thread.sleep(time);

                ProbeResponse response = new ProbeResponse(3, cpuLoad, freeRAM);
            }
        }
        catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
