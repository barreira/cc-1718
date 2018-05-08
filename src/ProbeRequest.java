import java.nio.ByteBuffer;

public class ProbeRequest {

    private final long timestamp;

    public ProbeRequest(long timestamp) {
        this.timestamp = timestamp;
    }

    public ProbeRequest(byte[] response) {
        ByteBuffer buffer = ByteBuffer.wrap(response);

        timestamp = buffer.getLong();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(8);

        buffer.putLong(timestamp);

        return buffer.array();
    }
}
