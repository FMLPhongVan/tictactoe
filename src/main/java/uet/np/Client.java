package uet.np;

import uet.np.bot.AI;
import uet.np.packet.Packet;
import uet.np.packet.PacketService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Client {
    //public static String address = "0.tcp.ap.ngrok.io";
    //public static int port = 16433;
    public static String address = "127.0.0.1";
    public static int port = 9001;
    public static final String KEY_MATCH = "asasdas";
    public static final int UID = 1;

    public static void main(String[] args) {
        try (Socket socket = new Socket(address, port)) {
            System.out.println("Connected to server");
            DataInputStream din = new DataInputStream(socket.getInputStream());
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());

            // Send packet PKT_HI
            Packet hiPacket = PacketService.initHiPacket(KEY_MATCH, UID);
            dout.write(PacketService.turnPacketToBytes(hiPacket), 0, hiPacket.getSize());
            System.out.println("Send packet Hi");
            int id = 0, l;
            boolean goFirst = true;
            boolean readyToPlay = false;
            AI ai = new AI();
            byte[] buffer = new byte[1000];

            boolean running = true;
            while (running) {
                int recvBytes = din.read(buffer);
                System.out.println("Received " + recvBytes + " bytes");
                if (recvBytes <= 0) {
                    break;
                }

                if (readyToPlay) {
                    ai.printBoard();
                }

                Packet recvPacket = PacketService.turnBytesToPacket(buffer);
                switch (recvPacket.type) {
                    case PKT_HI:
                        System.out.println("Received PKT_HI");
                        break;
                    case PKT_ID:
                        id = Utils.convertByteArrayToInt(recvPacket.data, 0);
                        goFirst = Utils.convertByteArrayToInt(recvPacket.data, 4) == 1;
                        System.out.println("Received PKT_ID");
                        System.out.println("ID: " + id);
                        System.out.println("You go first ?: " + goFirst);
                        break;
                    case PKT_BOARD:
                        ai.n = Utils.convertByteArrayToInt(recvPacket.data, 0);
                        ai.m = Utils.convertByteArrayToInt(recvPacket.data, 4);
                        ai.initBoard();
                        l = Utils.convertByteArrayToInt(recvPacket.data, 8);
                        ai.lengthToWin = Utils.convertByteArrayToInt(recvPacket.data, 12);
                        System.out.println("Received PKT_BOARD");
                        System.out.println("n: " + ai.n);
                        System.out.println("m: " + ai.m);
                        System.out.println("lengthToWin: " + ai.lengthToWin);
                        System.out.println("Blocked: " + l);
                        for (int i = 0; i < l; i++) {
                            int blocked = Utils.convertByteArrayToInt(recvPacket.data, 16 + i * 4);
                            ai.board[blocked / ai.n][blocked % ai.n] = -1;
                            System.out.format("Blocked %d: %d %d\n", blocked, blocked / ai.n, blocked % ai.n);
                        }

                        readyToPlay = true;

                        if (goFirst) {
                            int move = (int) (Math.random() * (ai.n * ai.m));
                            while (ai.board[move / ai.n][move % ai.n] != 0) {
                                move = (int) (Math.random() * (ai.n * ai.m));
                            }

                            ai.board[move / ai.n][move % ai.n] = 1;
                            System.out.format("My move: %d %d\n", move / ai.n, move % ai.n);

                            Packet sendPacket = PacketService.initSendPacket(id, move);
                            dout.write(PacketService.turnPacketToBytes(sendPacket), 0, sendPacket.getSize());
                            goFirst = false;
                        }

                        break;
                    case PKT_RECEIVE:
                        System.out.println("Received PKT_RECEIVE");
                        int move = Utils.convertByteArrayToInt(recvPacket.data, 0);
                        ai.board[move / ai.n][move % ai.n] = 2;

                        System.out.format("Opponent move: %d %d\n", move / ai.n, move % ai.n);
                        move = ai.nextMove();
                        System.out.format("My move: %d %d\n", move / ai.n, move % ai.n);
                        ai.board[move / ai.n][move % ai.n] = 1;

                        Packet sendPacket = PacketService.initSendPacket(id, move);
                        dout.write(PacketService.turnPacketToBytes(sendPacket), 0, sendPacket.getSize());
                        break;
                    case PKT_ERROR:
                        break;
                    case PKT_END:
                        running = false;
                        System.out.println("Received PKT_BYE");
                        break;
                    default:
                        System.out.println("Unknown packet type");
                        running = false;
                        break;
                }
            }

            din.close();
            dout.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
