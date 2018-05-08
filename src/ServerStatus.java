import java.net.InetAddress;

public class ServerStatus {

    private InetAddress ip;
    private int port;
    private int ram;
    private int cpu;
    private long rtt;
    private long devRTT;
    private float bandwidth;

    public ServerStatus(InetAddress ip, int port, int ram, int cpu, long rtt, long devRTT, float bandwidth) {
        this.ip = ip;
        this.port = port;
        this.ram = ram;
        this.cpu = cpu;
        this.rtt = rtt;
        this.devRTT = devRTT;
        this.bandwidth = bandwidth;
    }

    public InetAddress getIP() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getRAM() {
        return ram;
    }

    public int getCPU() {
        return cpu;
    }

    public long getRTT() {
        return rtt;
    }

    public long getDevRTT() {
        return devRTT;
    }

    public float getBandwidth() {
        return bandwidth;
    }
}
