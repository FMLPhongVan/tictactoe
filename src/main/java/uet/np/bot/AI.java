package uet.np.bot;

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
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 0) {
                    board[i][j] = 1;
                    score = minimax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                    System.out.println(i + " " + j + " " + score);
                    board[i][j] = 0;
                    if (score > bestScore) {
                        bestScore = score;
                        x = i;
                        y = j;
                    }
                }
            }
        }

        if (score == Integer.MIN_VALUE) {
            int move = (int) (Math.random() * (n * m));
            while (board[move / n][move % n] != 0 && board[move / n][move % n] != -1) {
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
                    }
                }
            }
        }

        return score;
    }

    public void printBoard() {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
}
