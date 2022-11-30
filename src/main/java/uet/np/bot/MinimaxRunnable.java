package uet.np.bot;

public class MinimaxRunnable implements Runnable {
    private int m;
    private int n;
    private int[][] board;
    private int depth;
    private int lengthToWin;
    public int atempt;
    final int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
    final int[] dy = {1, 1, 0, -1, -1, -1, 0, 1};
    public int BestScore = Integer.MIN_VALUE;

    public MinimaxRunnable(int m, int n, int[][] board, int depth, int lengthToWin, int atempt) {
        this.m = m;
        this.n = n;
        this.board = new int[m][n];
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < n; ++j) {
                this.board[i][j] = board[i][j];
            }
        }
        this.depth = depth;
        this.lengthToWin = lengthToWin;
        this.atempt = atempt;
    }


    @Override
    public void run() {
        BestScore = minimax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
    }

    private int minimax(int depth, int alpha, int beta, boolean isMaximizing) {
        if (depth == 0) {
            return evaluate();
        }

        if (isMaximizing) {
            int bestValue = Integer.MIN_VALUE;
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (board[i][j] == 0) {
                        board[i][j] = 1;
                        int value = minimax(depth - 1, alpha, beta, false);
                        board[i][j] = 0;
                        bestValue = Math.max(bestValue, value);
                        alpha = Math.max(alpha, value);
                        if (beta <= alpha) {
                            break;
                        }
                    }
                }
            }
            return bestValue;
        } else {
            int bestValue = Integer.MAX_VALUE;
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    if (board[i][j] == 0) {
                        board[i][j] = 2;
                        int value = minimax(depth - 1, alpha, beta, true);
                        board[i][j] = 0;
                        bestValue = Math.min(bestValue, value);
                        beta = Math.min(beta, value);
                        if (beta <= alpha) {
                            break;
                        }
                    }
                }
            }
            return bestValue;
        }
    }

    private int evaluate() {
        int score = 0;
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < n; ++j) {
                if (board[i][j] == 1)  {
                    for (int k = 0; k < 8; ++k) {
                        int x = i + dx[k];
                        int y = j + dy[k];
                        int count = 1;
                        while (x >= 0 && x < m && y >= 0 && y < n && board[x][y] == 1) {
                            count++;
                            x += dx[k];
                            y += dy[k];
                        }
                        if (count >= lengthToWin) {
                            return Integer.MAX_VALUE;
                        }
                        score += count * count;
                    }
                } else if (board[i][j] == 2) {
                    for (int k = 0; k < 8; ++k) {
                        int x = i + dx[k];
                        int y = j + dy[k];
                        int count = 1;
                        while (x >= 0 && x < m && y >= 0 && y < n && board[x][y] == 2) {
                            count++;
                            x += dx[k];
                            y += dy[k];
                        }
                        if (count >= lengthToWin) {
                            return Integer.MIN_VALUE;
                        }
                        score -= count * count;
                        if (count == lengthToWin - 2 || count == lengthToWin - 1) {
                            score -= count * count;
                        }
                    }
                }
            }
        }

        return score;
    }
}
