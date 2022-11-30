package uet.np.bot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AI {
    public int m;
    public int n;
    public int lengthToWin;
    public int[][] board;
    // 8 directions
    final int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
    final int[] dy = {1, 1, 0, -1, -1, -1, 0, 1};

    public AI() {

    }

    public AI(int m, int n, int lengthToWin) {
        this.m = m;
        this.n = n;
        this.lengthToWin = lengthToWin;
        board = new int[m][n];
    }

    public int nextMove() {
        int x = 0;
        int y = 0;
        int score = 0, bestScore = Integer.MIN_VALUE;
        int depth = 4;

        int atempt = 0;
        List<MinimaxThread> threads = new ArrayList<>();
        while (atempt < m * n) {
            if (board[atempt / n][atempt % n] != 0) {
                atempt++;
                continue;
            }

            if (!threads.isEmpty()) {
                for (int i = 0; i < threads.size(); i++) {
                    if (threads.get(i).getState() == Thread.State.TERMINATED) {
                        score = threads.get(i).minimaxRunnable.BestScore;
                        if (score > bestScore) {
                            bestScore = score;
                            x = threads.get(i).minimaxRunnable.atempt / n;
                            y = threads.get(i).minimaxRunnable.atempt % n;
                        }
                        threads.remove(threads.get(i));
                    }
                }
            }

            if (threads.size() < 5) {
                board[atempt / n][atempt % n] = 1;
                MinimaxRunnable t = new MinimaxRunnable(m, n, board, depth, lengthToWin, atempt);
                MinimaxThread thread = new MinimaxThread(t);
                thread.start();
                threads.add(thread);
                board[atempt / n][atempt % n] = 0;
                atempt++;
            }
        }

        if (bestScore == Integer.MIN_VALUE) {
            int move = (int) (Math.random() * (n * m));
            while (!(board[move / n][move % n] == 0)) {
                move = (int) (Math.random() * (n * m));
            }
            return move;
        }

        return x * n + y;
    }

    public void initBoard() {
        board = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = 0;
            }
        }
    }

    public void printBoard() {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == -1) {
                    System.out.print("  ");
                } else if (board[i][j] == 1) {
                    System.out.print("1 ");
                } else if (board[i][j] == 2) {
                    System.out.print("2 ");
                } else {
                    System.out.print("0 ");
                }
            }
            System.out.println();
        }
    }
}
