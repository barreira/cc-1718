import com.sun.management.OperatingSystemMXBean;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.util.Random;

public class UDPAgent {

    private static DatagramSocket socket;
    private static MulticastSocket multicastSocket;
    private static OperatingSystemMXBean os;

    private static final String MULTICAST_ADDRESS = "239.8.8.8";
    private static final int PORT = 8888;
    private static final String sharedKey = "cc1718g23";

    private static final int BUFFER_SIZE = 128;

    public UDPAgent() {
        socket = null;
        multicastSocket = null;
        os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    private static class ResponseHandler extends Thread {

        DatagramPacket packet;
        ProbeRequest request;

        public ResponseHandler(DatagramPacket packet, ProbeRequest request) {
            this.packet = packet;
            this.request = request;
        }

        public void run() {
            try {
                Random rand = new Random();
                int time = rand.nextInt(10) + 1;
                Thread.sleep(time * 1000);

                byte cpuUsage = (byte) (os.getSystemCpuLoad() * 100);
                int freeRam = (int) (os.getFreePhysicalMemorySize() >> 20); // B -> MB
                long timestamp = request.getTimestamp();

                ProbeResponse response = new ProbeResponse(cpuUsage, freeRam, timestamp);

                response.addHMAC(HMAC.generateHMAC(sharedKey, response.getData()));

                packet = new DatagramPacket(response.toByteArray(), response.toByteArray().length, packet.getAddress(),
                        packet.getPort());

                socket.send(packet);
                System.out.println("Sent: " + response.toString());
            }
            catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        socket = new DatagramSocket();
        multicastSocket = new MulticastSocket(PORT);

        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
        multicastSocket.joinGroup(group);

        os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        while (true) {
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            multicastSocket.receive(packet);

            ProbeRequest request = new ProbeRequest(packet.getData());
            System.out.println("Received: " + request.toString());

            if (HMAC.isAuthenticated(sharedKey, request.getHMAC(), request.getData())) {
                ResponseHandler rh = new ResponseHandler(packet, request);
                rh.start();
            }
            else {
                System.out.println("Received unauthenticated probe");
            }
        }

//        multicastSocket.leaveGroup(group);
//        multicastSocket.close();
    }
}
