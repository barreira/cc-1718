import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class ProbeResponse {

    private final byte cpuUsage;
    private final int freeRam;
    private final long timestamp;
    private final String hmac;

    public ProbeResponse(byte cpuUsage, int freeRam, long timestamp, String hmac) {
        this.cpuUsage = cpuUsage;
        this.freeRam = freeRam;
        this.timestamp = timestamp;
        this.hmac = hmac;
    }

    public ProbeResponse(byte[] response) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(response);

            cpuUsage = buffer.get();
            freeRam = buffer.getInt();
            timestamp = buffer.getLong();

            byte[] aux = new byte[buffer.remaining()];
            buffer.get(aux);
            hmac = new String(aux, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
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

    public String getHmac() {
        return hmac;
    }

    public byte[] getData() {
        ByteBuffer buffer = ByteBuffer.allocate(13);

        buffer.put(cpuUsage); // 1
        buffer.putInt(freeRam); // 4
        buffer.putLong(timestamp); // 8

        return buffer.array();
    }

    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(45);

        buffer.put(cpuUsage); // 1
        buffer.putInt(freeRam); // 4
        buffer.putLong(timestamp); // 8
        buffer.put(hmac.getBytes()); // 32

        return buffer.array();
    }
}
