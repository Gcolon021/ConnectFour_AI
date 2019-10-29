package c4.playersTest;
import c4.mvc.ConnectFourModel;
import c4.players.ConnectFourAIPlayer;

import java.util.Arrays;

public class ConnectFourAIPlayerTest {

    private static ConnectFourAIPlayer connectFourAIPlayer;

    public static void main(String args[]){
        ConnectFourModel connectFourModel = new ConnectFourModel();
        connectFourAIPlayer = new ConnectFourAIPlayer(connectFourModel);
//        testActionsMethod();
        testUtilityMethod();
    }

    public static void testActionsMethod(){
        int[][] grid = new int[7][6];
        for (int col = 0; col < grid.length; col++) {
            for (int row = 0; row < grid[0].length; row++) {
                if (col!=3 || col!=1){
                    grid[col][row] = -1;
                }
                if (col == 1){
                    grid[col][row] = 2;
                }
                if (col == 3){
                    grid[col][row] = 1;
                }
            }
        }

        int[] actions = connectFourAIPlayer.actions(grid);
        Arrays.stream(actions).forEach(System.out::println);
    }

    public static void testUtilityMethod(){
        int[][] grid = new int[7][6];
        for (int col = 0; col < grid.length; col++) {
            for (int row = 0; row < grid[0].length; row++) {
                if ( (col!=3 || row > 3)  || col!=1){
                    grid[col][row] = -1;
                }
                if (col == 1){
                    grid[col][row] = 2;
                }
                if (col == 3 && row <= 3){
                    grid[col][row] = 1;
                }
            }
        }
        int utility = connectFourAIPlayer.utility(grid);
        System.out.println(utility);
    }
}
