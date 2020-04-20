import java.util.Random;

public class BoardManager {

    private int NUMBER_OF_PIECES_PER_PLAYER = 5;
    private int TEAM_RED_CODE = 0;
    private int TEAM_GREEN_CODE = 1;
    private int TEAM_BLUE_CODE = 2;

    private int[][] directions = {
            {+1,  0}, { 0, -1}, {-1, -1},
            {-1,  0}, {0, +1}, { 1, 1},

    };

    private int[][] board = new int[9][9];
    private int[][] team_pieces = new int[NUMBER_OF_PIECES_PER_PLAYER][2];
    private int team_code;

    public BoardManager(){
        setUpTeams();
    }

    public void setTeamCode(int code){
        team_code = code;
        setUpOwnTeam();
    }

    public int[] chooseRandomPiece(){
        Random rand = new Random();
        return team_pieces[rand.nextInt(team_pieces.length)];
    }

    public int[][] getCurrentNeighbors(int[] position){
        int[][] neighbors = new int[6][2];
        for(int i = 0; i < directions.length; i++){
            neighbors[i] = new int[]{position[0] + directions[i][0], position[1] + directions[i][1]};
        }
        return neighbors;
    }

    private void setUpTeams(){
        for (int red = 0; red < NUMBER_OF_PIECES_PER_PLAYER; red++){
            board[red][0] = TEAM_RED_CODE;
        }
        for (int green = 0; green < NUMBER_OF_PIECES_PER_PLAYER; green++){
            board[green][green + 4] = TEAM_GREEN_CODE;
        }
        for (int blue = 0; blue < NUMBER_OF_PIECES_PER_PLAYER; blue++){
            board[8][blue + 4] = TEAM_BLUE_CODE;
        }
    }

    private void setUpOwnTeam(){
        int team_pieces_index = 0;
        for (int i = 0; i < board.length; i++){
            int[] boardRow = board[i];
            for (int j = 0; j < boardRow.length; j++){
                if(boardRow[j] == team_code){
                    team_pieces[team_pieces_index] = new int[]{i, j};
                    team_pieces_index++;
                }
            }
        }
    }
}
