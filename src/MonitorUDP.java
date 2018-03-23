import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MonitorUDP extends Thread {

    private TabelaEstado tabela;
    private MulticastSocket socket;
    private byte[] buffer;

    private static final int PROBING_INTERVAL = 10000;
    private static final String MULTICAST_ADDR = "239.8.8.8";
    private static final int MULTICAST_PORT = 8888;
    private static final int BUFFER_SIZE = 1024;
    private static final String PROBING_MSG = "Probing servers...";
    private static final int NUM_SERVERS = 3;

    MonitorUDP(TabelaEstado tabela) {
        this.tabela = tabela;
        socket = null;
        buffer = null;
    }

    public void run() {
        try {
            socket = new MulticastSocket(MULTICAST_PORT);

            InetAddress group = InetAddress.getByName(MULTICAST_ADDR);
            socket.joinGroup(group);

            while (true) {
                DatagramPacket packet = new DatagramPacket(PROBING_MSG.getBytes(), PROBING_MSG.length(), group, MULTICAST_PORT);
                socket.send(packet);

                for (int i = 0; i < NUM_SERVERS; i++) {
                    buffer = new byte[BUFFER_SIZE];
                    DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                    socket.receive(response);

                    // verificar integridade da resposta

                    // atualizar tabela com informação recebida
                }

                Thread.sleep(PROBING_INTERVAL);
            }
        }
        catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
