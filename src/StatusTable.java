import java.net.InetAddress;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class StatusTable {

    private Map<InetAddress, ServerStatus> servers;

    public StatusTable() {
        servers = new HashMap<>();
    }

    public ServerStatus chooseServer() {
        return null;
    }

    public synchronized void update(long sentTimestamp, long receivedTimestamp, InetAddress ip, int port, byte cpuUsage,
                                    int freeRAM) {

        long sampleRTT = sentTimestamp - receivedTimestamp;

        ServerStatus status = servers.get(ip);

        if (status == null) { // Ainda não havia nenhum registo do servidor
            status = new ServerStatus(ip, port, freeRAM, cpuUsage, sampleRTT, sampleRTT, 1, 0);
            servers.put(ip, status);
        }
        else {
            int newNumReceived = status.getNumReceived() + 1;
            long newTotalRTT = status.getTotalRTT() + sampleRTT;
            long newRTT = newTotalRTT / (long) newNumReceived;

            status = new ServerStatus(ip, port, freeRAM, cpuUsage, newRTT, newTotalRTT, newNumReceived,0);
            servers.put(ip, status);
        }
    }

    private static class ServerComparator implements Comparator<ServerStatus> {

        @Override
        public int compare(ServerStatus s1, ServerStatus s2) {
            return 0;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (ServerStatus ss : servers.values()) {
            sb.append(ss.toString());
            sb.append("\n");
        }

        return sb.toString();
    }
}