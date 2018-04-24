import java.net.DatagramSocket;
import java.net.SocketException;

public class MonitorSocket {

    private DatagramSocket socket;

    public MonitorSocket(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }

    public void send() {

    }

    public PDU receive() {

    }
}
