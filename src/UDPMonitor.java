import java.io.IOException;
import java.net.*;

public class UDPMonitor {

    private static StatusTable table;

    private static final int PROBING_INTERVAL = 5000;
    private static final String MULTICAST_ADDR = "239.8.8.8";
    private static final int MULTICAST_PORT = 8888;
    private static final String sharedKey = "cc1718g23";

    private static final int BUFFER_SIZE = 128;

    UDPMonitor(StatusTable table) {
        this.table = table;
    }

    private static class Prober extends Thread {

        private final DatagramSocket socket;

        private Prober(DatagramSocket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                while (true) {
                    InetAddress group = InetAddress.getByName(MULTICAST_ADDR);
                    long timestamp = System.currentTimeMillis();

                    ProbeRequest request = new ProbeRequest(timestamp);

                    request.addHMAC(HMAC.generateHMAC(sharedKey, request.getData()));

                    DatagramPacket packet = new DatagramPacket(request.toByteArray(), request.toByteArray().length,
                                                               group, MULTICAST_PORT);

                    socket.send(packet);
                    System.out.println("Sent: " + request.toString());

                    sleep(PROBING_INTERVAL);
                }
            }
            catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class ResponseHandler extends Thread {

        private final DatagramSocket socket;
        private final DatagramPacket packet;
        private final long receivedTimestamp;

        private ResponseHandler(DatagramSocket socket, DatagramPacket packet, long receivedTimestamp) {
            this.socket = socket;
            this.packet = packet;
            this.receivedTimestamp = receivedTimestamp;
        }

        public void run() {

            ProbeResponse response = new ProbeResponse(packet.getData());

            if (HMAC.isAuthenticated(sharedKey, response.getHMAC(), response.getData())) { // verificar integridade da resposta

                System.out.println("\nReceived: " + response.toString());

                // atualizar table com informação recebida

                table.update(receivedTimestamp, response.getTimestamp(), packet.getAddress(), packet.getPort(),
                             response.getCpuUsage(), response.getFreeRam());

                System.out.println(table.toString() + "\n");
            }
            else {
                System.out.println("Received unauthenticated response");
            }
        }
    }

    public static void main(String[] args) throws IOException {

        try {
            table = new StatusTable();

            DatagramSocket socket = new DatagramSocket();

            Prober p = new Prober(socket);
            p.start();

            while (true) {
                byte[] buffer = new byte[BUFFER_SIZE];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);

                ResponseHandler rh = new ResponseHandler(socket, response, System.currentTimeMillis());
                rh.start();
            }
        }
        catch (IOException e) {
            throw new IOException();
        }

    }
}
