package uet.np;

import java.util.List;

public class Utils {
    public static byte[] convertIntToByteArray(int value, boolean isLittleEndian) {
        if (isLittleEndian) {
            return new byte[]{
                    (byte) (value & 0xff),
                    (byte) ((value >> 8) & 0xff),
                    (byte) ((value >> 16) & 0xff),
                    (byte) ((value >> 24) & 0xff)
            };
        } else {
            return new byte[]{
                    (byte) ((value >> 24) & 0xff),
                    (byte) ((value >> 16) & 0xff),
                    (byte) ((value >> 8) & 0xff),
                    (byte) (value & 0xff)
            };
        }
    }

    public static byte[] convertIntToByteArray(int value) {
        return convertIntToByteArray(value, true);
    }

    public static int convertByteArrayToInt(byte[] buffer, int i, boolean isLittleEndian) {
        if (isLittleEndian) {
            return (buffer[i] & 0xff) | ((buffer[i + 1] & 0xff) << 8) | ((buffer[i + 2] & 0xff) << 16) | ((buffer[i + 3] & 0xff) << 24);
        } else {
            return ((buffer[i] & 0xff) << 24) | ((buffer[i + 1] & 0xff) << 16) | ((buffer[i + 2] & 0xff) << 8) | (buffer[i + 3] & 0xff);
        }
    }

    public static int convertByteArrayToInt(byte[] buffer, int i) {
        return convertByteArrayToInt(buffer, i, true);
    }

    public static byte[] testWebServer() {
        List<byte[]> bytes = new java.util.ArrayList<byte[]>();
        bytes.add(convertIntToByteArray(1));
        bytes.add(convertIntToByteArray(6));
        bytes.add("123123".getBytes());
        bytes.add(convertIntToByteArray(8888));
        bytes.add(convertIntToByteArray(9));
        bytes.add("tictactoe".getBytes());
        bytes.add(convertIntToByteArray(1));
        bytes.add("a".getBytes());
        bytes.add(convertIntToByteArray(2));
        bytes.add("bb".getBytes());

        // convert list byte[] to byte[], merge it
        int totalLength = 0;
        for (byte[] b : bytes) {
            totalLength += b.length;
        }
        byte[] result = new byte[totalLength];
        int offset = 0;
        for (byte[] b : bytes) {
            System.arraycopy(b, 0, result, offset, b.length);
            offset += b.length;
        }
        for (byte b : result) {
            System.out.print(b + " ");
        }
        return result;
    }
}
