package uet.np.packet;

import uet.np.Utils;

public class PacketService {
    public static Packet initHiPacket(final String KEY_MATCH, final int UID) {
        byte[] data = new byte[4 + KEY_MATCH.length()];
        System.arraycopy(Utils.convertIntToByteArray(UID), 0, data, 0, 4);
        System.arraycopy(KEY_MATCH.getBytes(), 0, data, 4, KEY_MATCH.length());
        return new Packet(PacketType.PKT_HI, data.length, data);
    }

    public static Packet initSendPacket(final int id, final int move) {
        byte[] data = new byte[8];
        System.arraycopy(Utils.convertIntToByteArray(id), 0, data, 0, 4);
        System.arraycopy(Utils.convertIntToByteArray(move), 0, data, 4, 4);
        return new Packet(PacketType.PKT_SEND, data.length, data);
    }

    public static byte[] turnPacketToBytes(Packet packet) {
        byte[] bytes = new byte[packet.getSize()];
        System.arraycopy(Utils.convertIntToByteArray(packet.type.ordinal()), 0, bytes, 0, 4);
        System.arraycopy(Utils.convertIntToByteArray(packet.len), 0, bytes, 4, 4);
        System.arraycopy(packet.data, 0, bytes, 8, packet.len);
        return bytes;
    }

    public static Packet turnBytesToPacket(byte[] buffer, int offset) {
        int type = Utils.convertByteArrayToInt(buffer, offset, true);
        int len = Utils.convertByteArrayToInt(buffer, offset + 4, true);
        byte[] data = new byte[len];
        System.arraycopy(buffer, offset + 8, data, 0, len);
        if (type > PacketType.values().length - 1 || type < 0) {
            type = PacketType.UNKNOWN.ordinal();
        }
        return new Packet(PacketType.values()[type], len, data);
    }
}
