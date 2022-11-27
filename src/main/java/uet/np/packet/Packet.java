package uet.np.packet;

public class Packet {
    public PacketType type;
    public int len;
    public byte[] data;

    public Packet(PacketType type, int len, byte[] data) {
        this.type = type;
        this.len = len;
        this.data = data;
    }

    public int getSize() {
        return 8 + len;
    }
}
