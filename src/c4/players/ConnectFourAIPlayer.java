package c4.players;

import c4.mvc.ConnectFourModelInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

import static java.lang.Math.*;

public class ConnectFourAIPlayer extends ConnectFourPlayer {

    private final int EMPTY = -1;
    private ConnectFourModelInterface model;
    private int currentPlayerPosition;
    private int maxDepth;

    public ConnectFourAIPlayer(ConnectFourModelInterface model) {
        this.model = model;
        this.maxDepth = 100;
    }

    public ConnectFourAIPlayer(ConnectFourModelInterface model, int depth) {
        this.model = model;
        this.maxDepth = depth;
    }

    @Override
    public int getMove() {
        if (currentPlayerPosition == 0) {
            currentPlayerPosition = model.getTurn();
        }
        int[][] grid = model.getGrid();
        return alphaBetaSearch(grid);
    }

    private int alphaBetaSearch(int[][] state) {
        int col = 3;
        int[] actions = actions(state);
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < actions.length; i++) {
            int x = actions[i];
            int i1 = minValue(result(state, x), Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
            if (i1 > max) {
                max = i1;
                col = x;
            }
        }
        return col;
    }

    public int maxValue(int[][] state, int alpha, int beta, int currentDepth) {
        if (terminalTest(state, currentDepth)) return utility(state);
        int[] actions = actions(state);
        int eval = Integer.MIN_VALUE;
        for (int i = 0; i < actions.length; i++) {
            int action = actions[i];
            eval = max(eval, minValue(result(state, action), alpha, beta, currentDepth + 1));
            if (eval >= beta) return eval;
            alpha = max(alpha, eval);
            }
        return eval;
    }

    private int minValue(int[][] state, int alpha, int beta, int currentDepth) {
        if (terminalTest(state, currentDepth)) return utility(state);
        int[] actions = actions(state);
        int eval = Integer.MAX_VALUE;
        for (int i = 0; i < actions.length; i++) {
            int action = actions[i];
            eval = min(eval, maxValue(result(state, action), alpha, beta, currentDepth + 1));
            if (eval >= beta) return eval;
            beta = min(beta, eval);
            }
        return eval;
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
        ArrayList<Integer> actions = new ArrayList<>();
        for (int i = 0; i < board.length; i++) if (board[i][0] == EMPTY) actions.add(i);
        int[] possibleActions = new int[actions.size()];
        for (int i = 0; i < possibleActions.length; i++) {
            possibleActions[i] = actions.get(i);
        }
        return possibleActions;
    }

    private boolean terminalTest(int[][] state, int depth) {
        return checkForWinner(state) || checkForDraw(state) || depth == this.maxDepth;
    }

    private int[][] result(int[][] gameState, int action) {
        int[][] grid = new int[gameState.length][gameState[0].length];
        IntStream.range(0, grid.length).forEachOrdered(i -> System.arraycopy(gameState[i], 0, grid[i], 0, grid[0].length));
        for (int row = 0; row < grid[0].length; row++) {
            if (grid[action][row] == -1) {
                grid[action][row] = determinePlayer(gameState);
            }
        }
        return grid;
    }

    private int[][] undoResult(int[][] gameState, int action) {
        int[][] grid = gameState.clone();
        for (int row = 1; row < grid[0].length; row++) {
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
        int player = determinePlayer(state);
        if (player == 1)
            return 1000;
        else if (player == 2){
            return -1000;
        }
        else if (checkForDraw(state)){
            return 0;
        }
        else return 0;
//        else return checkForMidGameUtility(state); // will get here at when max depth < 42
    }

    private int checkForMidGameUtility(int[][] gameBoard) {
        int total = 0;
        for (int col = 0; col < gameBoard.length; col++) {
            for (int row = 0; row < gameBoard[0].length; row++) {
                int horizontalTemp = 0;
                int verticalTemp = 0;
                int posDiagTemp = 0;
                int negDiagTemp = 0;
                for (int i = 0; i <= 2; i++) {
                    //Horizontal
                    if (col <= 3) {
                        if (gameBoard[col + i][row] == model.getTurn()) {
                            horizontalTemp += 1;
                        }
                    }
                    //Vertical
                    if (row <= 2) {
                        if (gameBoard[col][row + i] == model.getTurn()) {
                            verticalTemp += 1;
                        }
                    }
                    //Pos Diagonal
                    if (row <= 2 && col >= 3) {
                        if (gameBoard[col - i][row + i] == model.getTurn()) {
                            posDiagTemp += 1;
                        }
                    }
                    //Neg Diagonal
                    if (row <= 2 && col <= 3) {
                        if (gameBoard[col + i][row + i] == model.getTurn()) {
                            negDiagTemp += 1;
                        }
                    }
                }

                //the reason for squaring is to make the function value multiple in a row even more appealing to alpha-beta
                total += sqrt(horizontalTemp) + sqrt(verticalTemp) + sqrt(posDiagTemp) + sqrt(negDiagTemp);
            }
        }
        return total;
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
