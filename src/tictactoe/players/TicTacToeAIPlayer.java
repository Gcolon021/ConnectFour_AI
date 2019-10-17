package tictactoe.players;

import tictactoe.mvc.TicTacToeModel;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class TicTacToeAIPlayer extends TicTacToePlayer {
    TicTacToeModel model;
    char symbol;

    public TicTacToeAIPlayer(TicTacToeModel model, char symbol) {
        this.model = model;
        this.symbol = symbol;
    }

    // Assume actions are numbered 1-9
    public char[][] result(char[][] state, int action) {
        // Deep copy the state
        char[][] newstate = new char[3][3];
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                newstate[row][col] = state[row][col];

        char turn = this.getTurn(state);

        action -= 1;
        int col = action % 3;
        int row = action / 3;
        newstate[row][col] = turn;

        return newstate;
    }

    public int[] actions(char[][] state) {
        ArrayList<Integer> moves = new ArrayList<Integer>();
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                if (state[row][col] == '-')
                    moves.add(row * 3 + col + 1);

        int[] results = new int[moves.size()];
        for (int i = 0; i < results.length; i++)
            results[i] = moves.get(i);
        return results;
    }

    public boolean terminalTest(char[][] state) {
        for (int row = 0; row < 3; row++) {
            if (state[row][0] != '-' && state[row][0] == state[row][1] && state[row][0] == state[row][2])
                return true;
        }
        for (int col = 0; col < 3; col++) {
            if (state[0][col] != '-' && state[0][col] == state[1][col] && state[0][col] == state[2][col])
                return true;
        }
        if (state[0][0] != '-' && state[0][0] == state[1][1] && state[0][0] == state[2][2])
            return true;
        if (state[2][0] != '-' && state[2][0] == state[1][1] && state[2][0] == state[0][2])
            return true;

        return isDraw(state);
    }

    public int utility(char[][] state) {
        if (getWinner(state) == symbol)
            return 1000;
        else if (getWinner(state) != '-')
            return -1000;
        else if (isDraw(state))
            return 0;

        return 0; //should not happen
    }

    protected boolean isDraw(char[][] state) {
        boolean allFilled = true;
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                if (state[row][col] == '-')
                    allFilled = false;
        return allFilled;
    }

    protected char getWinner(char[][] state) {
        for (int row = 0; row < 3; row++) {
            if (state[row][0] != '-' && state[row][0] == state[row][1] && state[row][0] == state[row][2])
                return state[row][0];
        }
        for (int col = 0; col < 3; col++) {
            if (state[0][col] != '-' && state[0][col] == state[1][col] && state[0][col] == state[2][col])
                return state[0][col];
        }
        if (state[0][0] != '-' && state[0][0] == state[1][1] && state[0][0] == state[2][2])
            return state[0][0];
        if (state[2][0] != '-' && state[2][0] == state[1][1] && state[2][0] == state[0][2])
            return state[2][0];

        return '-'; // Should not happen
    }

    protected char getTurn(char[][] state) {
        int empties = 0;
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                if (state[row][col] == '-')
                    empties++;

        if (empties % 2 == 1)
            return 'X';
        else
            return 'O';
    }

    @Override
    public int getMove() {
        return alphaBetaSearch(model.getGrid());
    }

    private int alphaBetaSearch(char[][] state) {
        int[] actions = actions(state);
        int col = 0;
        int max = 0;
        for (int i = 0; i < actions.length; i++) {
            int x = actions[i];
            int i1 = maxValue(result(state, x), Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (max < i1) {
                max = i1;
                col = x;
            }
        }
        System.out.println(col);
        return col;
    }

    public int maxValue(char[][] state, int alpha, int beta) {
        if (terminalTest(state)) return utility(state);
        int[] actions = actions(state);
        int eval = Integer.MIN_VALUE;
        for (int i = 0; i < actions.length; i++) {
            int action = actions[i];
            eval = max(eval, minValue(result(state, action), alpha, beta));
            if (eval >= beta) return eval;
            alpha = max(alpha, eval);
        }
        return eval;
    }

    private int minValue(char[][] state, int alpha, int beta) {
        if (terminalTest(state)) return utility(state);
        int[] actions = actions(state);
        int eval = Integer.MAX_VALUE;
        for (int i = 0; i < actions.length; i++) {
			int action = actions[i];
			eval = max(eval, maxValue(result(state, action), alpha, beta));
            if (eval >= alpha) return eval;
            beta = min(beta, eval);
        }
        return eval;
    }
}
