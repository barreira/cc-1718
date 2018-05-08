import com.sun.management.OperatingSystemMXBean;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.util.Random;

public class AgentUDP extends Thread {

    private static DatagramSocket socket;
    private static MulticastSocket multicastSocket;
    private static OperatingSystemMXBean os;

    private static final String MULTICAST_ADDRESS = "239.8.8.8";
    private static final int PORT = 8888;
    private static final int BUFFER_SIZE = 128;

    public AgentUDP() {
        socket = null;
        multicastSocket = null;
        os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    public static void main(String[] args) {
        try {
            socket = new DatagramSocket();
            multicastSocket = new MulticastSocket(PORT);

            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            multicastSocket.joinGroup(group);

            while (true) {

                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(packet);

                InetAddress monitorAddress = packet.getAddress();
                int monitorPort = packet.getPort();

                System.out.println("Recebi packet: " + monitorAddress.toString());

                ProbeRequest request = new ProbeRequest(packet.getData());

//                byte cpuUsage = (byte) (os.getSystemCpuLoad() * 100);
//                int freeRam = (int) (os.getFreePhysicalMemorySize() >> 20); // B -> MB
                byte cpuUsage = (byte) 50;
                int freeRam = 1024;
                long timestamp = request.getTimestamp();

                ProbeResponse response = new ProbeResponse(cpuUsage, freeRam, timestamp);
                packet = new DatagramPacket(response.toByteArray(), response.toByteArray().length, monitorAddress, monitorPort);

                Random rand = new Random();
                int time = rand.nextInt(10) + 1;
                Thread.sleep(time);

                socket.send(packet);
                System.out.println("Sent probing response");
            }

//            multicastSocket.leaveGroup(group);
//            multicastSocket.close();
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
