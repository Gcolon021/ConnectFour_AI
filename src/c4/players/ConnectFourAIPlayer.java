package c4.players;

import c4.mvc.ConnectFourModel;
import c4.mvc.ConnectFourModelInterface;

import java.util.Arrays;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class ConnectFourAIPlayer extends ConnectFourPlayer {

    private final int EMPTY = -1;
    private ConnectFourModel model;
    private int playerTurn = 1;
    private int depth = 42;

    public ConnectFourAIPlayer(ConnectFourModelInterface model) {
        this.model = (ConnectFourModel) model;
    }

    @Override
    public int getMove() {
        int[][] state = model.getGrid();
        int[] actions = actions(state);
        int col = -1;
        int max = 0;
        for (int i = 0; i < actions.length; i++) {
            int x = actions[i];
            if (x != 0) {
                int i1 = alphaBetaPruning(Integer.MIN_VALUE, Integer.MAX_VALUE, result(state, i), depth, true);
                if (max < i1){
                    max = i1;
                    col = i;
                }
            }
        }
        return col;
    }

    private int alphaBetaPruning(int alpha, int beta, int[][] state, int depth, Boolean maximizingPlayer) {
        if (checkForWinner(state) || depth == -1) return utility(state);
        int[] actions = actions(state);
        int eval = -1;
        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < actions.length; i++) {
                if (actions[i] != 0) {
                    eval = alphaBetaPruning(alpha, beta, result(state, i), depth - 1, false);
                    alpha = max(alpha, eval);
                    if (alpha >= beta) break;
                }
                return eval;
            }
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < actions.length; i++) {
                if (actions[i] != 0) {
                    eval = alphaBetaPruning(alpha, beta, result(state, i), depth - 1, true);
                    beta = min(beta, eval);
                    if (alpha >= beta) break;
                }
                return eval;
            }
        }
        return 0;
    }

    public int dumbGetMove() {
        boolean[] moves = model.getValidMoves();
        int[][] grid = model.getGrid();

        for (int col = 0; col < grid.length; col++) {
            for (int row = 0; row < grid[1].length; row++) {
                if (col < 7 && row <= 2) {
                    if ((grid[col][row] == model.getTurn() || grid[col][row] == -1)
                            && (grid[col][row + 1] == -1 || grid[col][row + 1] == model.getTurn())
                            && (grid[col][row + 2] == -1 || grid[col][row + 2] == model.getTurn())
                            && (grid[col][row + 3] == -1) || grid[col][row + 3] == model.getTurn()
                            && moves[col] != false) {
                        return col;
                    }
                } else if (col >= 3 && row <= 2) {
                    if ((grid[col][row] == model.getTurn() || grid[col][row] == -1)
                            && (grid[col - 1][row + 1] == model.getTurn() || grid[col - 1][row + 1] == -1)
                            && (grid[col - 2][row + 2] == model.getTurn() || grid[col - 2][row + 2] == -1)
                            && (grid[col - 3][row + 3] == model.getTurn() || grid[col - 3][row + 3] == -1)
                            && moves[col] != false) {
                        return col;
                    }
                } else if (col <= 3 && row <= 2) {
                    if ((grid[col][row] == model.getTurn() || grid[col][row] == -1)
                            && (grid[col + 1][row + 1] == model.getTurn() || grid[col + 1][row + 1] == -1)
                            && (grid[col + 2][row + 2] == model.getTurn() || grid[col + 2][row + 2] == -1)
                            && (grid[col + 3][row + 3] == model.getTurn() || grid[col + 3][row + 3] == -1)
                            && moves[col] != false) {
                        return col;
                    }
                } else if (col <= 3 && row < 6) {
                    if ((grid[col][row] == model.getTurn() || grid[col][row] == -1)
                            && (grid[col + 1][row] == model.getTurn() || grid[col + 1][row] == -1)
                            && (grid[col + 2][row] == model.getTurn() || grid[col + 2][row] == -1)
                            && (grid[col + 3][row] == model.getTurn() || grid[col + 3][row] == -1)
                            && moves[col] != false) {
                        return col;
                    }
                }
            }
        }
        return 0;
    }

    private int[] actions(int[][] board) {
        int[] actions = new int[7];
        for (int i = 0; i < board.length; i++) if (board[i][0] == EMPTY) actions[i] = 1;
        return actions;
    }

    private boolean terminalTest() {
        return model.checkForWinner() > 0 || model.checkForDraw();
    }

    private int[][] result(int[][] gameState, int action) {
        int[][] grid = gameState.clone();
        for (int row = grid[0].length - 1; row > 0; row--) {
            if (grid[action][row] == -1) {
                grid[action][row] = determinePlayer(gameState);
            }
        }
        return grid;
    }

    private int determinePlayer(int[][] gameBoard) {
        //count number of occurrences if players != return player 2 else return player 1
        int player1 = 0;
        int player2 = 0;
        for (int col = 0; col < gameBoard.length; col++) {
            for (int row = 0; row < gameBoard[0].length; row++) {
                if (gameBoard[col][row] == 1) {
                    player1++;
                } else if (gameBoard[col][row] == 2) {
                    player2++;
                }
            }
        }
        return player1 != player2 ? 2 : 1;
    }

    public int utility(int[][] state) {
        if (checkForWinner(state))
            return 1000;
        else if (checkForWinner(state))
            return -1000;
        else if (checkForDraw(state))
            return 0;
        return 0; //should not happen
    }

    public boolean checkForWinner(int[][] grid) {
        int winResult = checkHorizontalWin(grid);
        if (winResult < 0)
            winResult = checkVerticalWin(grid);
        if (winResult < 0)
            winResult = checkNegDiagonalWin(grid);
        if (winResult < 0)
            winResult = checkPosDiagonalWin(grid);
        // Must not have one
        return winResult > 0;
    }

    private int checkPosDiagonalWin(int[][] grid) {
        boolean win = false;
        for (int col = 3; col < 7; col++) {
            for (int row = 0; row <= 2; row++) {
                if (grid[col][row] != EMPTY) {
                    win = (grid[col][row] == grid[col - 1][row + 1]) && (grid[col][row] == grid[col - 2][row + 2]) && (grid[col][row] == grid[col - 3][row + 3]);
                }
                if (win) return grid[col][row];
            }
        }
        return -1;
    }

    private int checkNegDiagonalWin(int[][] grid) {
        boolean win = false;
        for (int col = 0; col <= 3; col++) {
            for (int row = 0; row <= 2; row++) {
                if (grid[col][row] != EMPTY) {
                    win = (grid[col][row] == grid[col + 1][row + 1]) && (grid[col][row] == grid[col + 2][row + 2]) && (grid[col][row] == grid[col + 3][row + 3]);
                }
                if (win) return grid[col][row];
            }
        }
        return -1;
    }

    private int checkVerticalWin(int[][] grid) {
        boolean win = false;
        for (int col = 0; col < 7; col++) {
            for (int row = 0; row <= 2; row++) {
                if (grid[col][row] != EMPTY) {
                    win = (grid[col][row] == grid[col][row + 1]) && (grid[col][row] == grid[col][row + 2]) && (grid[col][row] == grid[col][row + 3]);
                }
                if (win) return grid[col][row];
            }
        }
        return -1;
    }

    private int checkHorizontalWin(int[][] grid) {
        boolean win = false;
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col <= 3; col++) {
                if (grid[col][row] != EMPTY) {
                    win = (grid[col][row] == grid[col + 1][row]) && (grid[col][row] == grid[col + 2][row]) && (grid[col][row] == grid[col + 3][row]);
                }
                if (win) return grid[col][row];
            }
        }
        return -1;
    }

    public boolean checkForDraw(int[][] grid) {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                if (grid[i][j] == EMPTY)
                    return false;
            }
        }
        return true;
    }

}
