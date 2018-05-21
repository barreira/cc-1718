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

    public synchronized boolean update(long receivedTimestamp, long sentTimestamp, InetAddress ip, int port,
                                       byte cpuUsage, int freeRAM) {
        boolean newer = true;
        long sampleRTT = receivedTimestamp - sentTimestamp;

        ServerStatus status = servers.get(ip);

        if (status == null) { // Ainda não havia nenhum registo do servidor
            status = new ServerStatus(ip, port, sentTimestamp, freeRAM, cpuUsage, sampleRTT, sampleRTT, 1, 0);
        }
        else {
            int newNumReceived = status.getNumReceived() + 1;
            long newTotalRTT = status.getTotalRTT() + sampleRTT;
            long newRTT = newTotalRTT / (long) newNumReceived;

            if (sentTimestamp > status.getTimestamp()) { // caso seja informação mais recente acerca do servidor
                status = new ServerStatus(ip, port, sentTimestamp, freeRAM, cpuUsage, newRTT, newTotalRTT,
                                          newNumReceived,0);
            }
            else { // informação recebida é mais antiga do que a que já existia
                newer = false;

                long oldSentTimestamp = status.getTimestamp();
                int oldFreeRAM = status.getRAM();
                int oldCpuUsage = status.getCPU();

                status = new ServerStatus(ip, port, oldSentTimestamp, oldFreeRAM, oldCpuUsage, newRTT, newTotalRTT,
                                          newNumReceived, 0);
            }
        }

        servers.put(ip, status);

        return newer;
    }

    private static class ServerComparator implements Comparator<ServerStatus> {

        @Override
        public int compare(ServerStatus s1, ServerStatus s2) {
            return 0;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("StatusTable(" );
        sb.append("\n");

        for (ServerStatus ss : servers.values()) {
            sb.append("\t");
            sb.append(ss.toString());
            sb.append(";");
            sb.append("\n");
        }

        sb.append(")");

        return sb.toString();
    }
}