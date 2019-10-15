package c4.players;

import c4.mvc.ConnectFourModelInterface;

import java.util.Random;

public class ConnectFourAIPlayer extends ConnectFourPlayer {
    ConnectFourModelInterface model;
    Random random;

    public ConnectFourAIPlayer(ConnectFourModelInterface model) {
        this.model = model;
    }

    @Override
    public int getMove() {
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
}
