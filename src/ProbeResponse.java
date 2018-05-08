import java.nio.ByteBuffer;

public class ProbeResponse {

    private final byte cpuUsage;
    private final int freeRam;
    private final long timestamp;

    public ProbeResponse(byte cpuUsage, int freeRam, long timestamp) {
        this.cpuUsage = cpuUsage;
        this.freeRam = freeRam;
        this.timestamp = timestamp;
    }

    public ProbeResponse(byte[] response) {
        ByteBuffer buffer = ByteBuffer.wrap(response);

        cpuUsage = buffer.get();
        freeRam = buffer.getInt();
        timestamp = buffer.getLong();
    }

    public byte getCpuUsage() {
        return cpuUsage;
    }

    public int getFreeRam() {
        return freeRam;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(13);

        buffer.put(cpuUsage);
        buffer.putInt(freeRam);
        buffer.putLong(timestamp);

        return buffer.array();
    }
}
