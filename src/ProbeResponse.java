public class ProbeResponse {

    private final int tipo;
    private final byte cpu_usage;
    private final int free_ram;

    public ProbeResponse(int tipo, byte cpu_usage, int free_ram) {
        this.tipo = tipo;
        this.cpu_usage = cpu_usage;
        this.free_ram = free_ram;
    }
}
