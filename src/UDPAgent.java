import com.sun.management.OperatingSystemMXBean;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class UDPAgent {

    private static DatagramSocket socket;
    private static MulticastSocket multicastSocket;
    private static OperatingSystemMXBean os;

    private static final String MULTICAST_ADDRESS = "239.8.8.8";
    private static final int PORT = 8888;
    private static final int BUFFER_SIZE = 128;
    private static final String sharedKey = "cc1718g23";

    public UDPAgent() {
        socket = null;
        multicastSocket = null;
        os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    private static boolean isAuthenticated(ProbeRequest request) {
        try {
            Mac hasher = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(sharedKey.getBytes("UTF-8"), "HmacSHA256");
            hasher.init(key);

            byte[] result = hasher.doFinal(request.getData());
            String resultStr = new String(result, "UTF-8");

            return resultStr.equals(request.getHMAC());
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            socket = new DatagramSocket();
            multicastSocket = new MulticastSocket(PORT);

            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            multicastSocket.joinGroup(group);

            os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

            while (true) {
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(packet);

                InetAddress monitorAddress = packet.getAddress();
                int monitorPort = packet.getPort();

                System.out.println("Recebi packet: " + monitorAddress.toString());

                ProbeRequest request = new ProbeRequest(packet.getData());

                if (isAuthenticated(request)) {
                    byte cpuUsage = (byte) (os.getSystemCpuLoad() * 100);
                    int freeRam = (int) (os.getFreePhysicalMemorySize() >> 20); // B -> MB
//                byte cpuUsage = (byte) 50;
//                int freeRam = 1024;
                    long timestamp = request.getTimestamp();

                    ProbeResponse response = new ProbeResponse(cpuUsage, freeRam, timestamp);
                    packet = new DatagramPacket(response.toByteArray(), response.toByteArray().length, monitorAddress, monitorPort);

                    Random rand = new Random();
                    int time = rand.nextInt(10) + 1;
                    Thread.sleep(time);

                    socket.send(packet);
                    System.out.println("Sent probing response");
                }
                else {
                    System.out.println("Received unauthenticated probe");
                }
            }

//            multicastSocket.leaveGroup(group);
//            multicastSocket.close();
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
