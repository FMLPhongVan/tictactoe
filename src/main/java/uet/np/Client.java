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
    public static String address = "s.vominhduc.me";
    public static int port = 9000;
    public static String KEY_MATCH = "123";
    public static int UID = 44403;

    public static void main(String[] args) {
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
            if (args.length > 1) UID = Integer.parseInt(args[1]);
            if (args.length > 2) KEY_MATCH = args[2];
        }
        System.out.println("Port: " + port);
        System.out.println("UID: " + UID);
        System.out.println("KEY_MATCH: " + KEY_MATCH);
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

                        ai.printBoard();

                        break;
                    case PKT_RECEIVE:
                        System.out.println("Received PKT_RECEIVE");
                        int move = Utils.convertByteArrayToInt(recvPacket.data, 0);
                        ai.board[move / ai.n][move % ai.n] = 2;

                        System.out.format("Opponent move: %d %d\n", move / ai.n, move % ai.n);
                        move = ai.nextMove();
                        System.out.format("My move: %d %d\n", move / ai.n, move % ai.n);
                        ai.board[move / ai.n][move % ai.n] = 1;

                        if (readyToPlay) {
                            ai.printBoard();
                        }

                        Packet sendPacket = PacketService.initSendPacket(id, move);
                        dout.write(PacketService.turnPacketToBytes(sendPacket), 0, sendPacket.getSize());
                        break;
                    case PKT_ERROR:
                        System.out.println("Received PKT_ERROR");
                        do {
                            move = (int) (Math.random() * (ai.n * ai.m));
                        } while (ai.board[move / ai.n][move % ai.n] != 0);

                        sendPacket = PacketService.initSendPacket(id, move);
                        dout.write(PacketService.turnPacketToBytes(sendPacket), 0, sendPacket.getSize());
                        break;
                    case PKT_END:
                        System.out.println("Received PKT_END");
                        running = false;
                        int winnerId = Utils.convertByteArrayToInt(recvPacket.data, 0);
                        if (winnerId == id) {
                            System.out.println("You win");
                        } else if (winnerId == 0) {
                            System.out.println("Draw");
                        } else {
                            System.out.println("You lose");
                        }
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
