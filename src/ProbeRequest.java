public class ProbeRequest {

    private final byte type;
    private final long timestamp;

    public ProbeRequest(byte type, long timestamp) {
        this.type = type;
        this.timestamp = timestamp;
    }

}
