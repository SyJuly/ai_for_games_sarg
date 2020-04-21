import lenz.htw.sarg.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoardManager {

    private int NUMBER_OF_PIECES_PER_PLAYER = 5;

    private int[][] directions = {
            {+1,  0}, { 0, -1}, {-1, -1},
            {-1,  0}, {0, +1}, { 1, 1},

    };

    private Team[] teams;
    private int[][] board = new int[9][9];
    private List<int[]> team_pieces = new ArrayList<>();
    private Team own_team;

    public BoardManager(){
        teams = new Team[3];
        teams[TeamCode.RED.getCode()] = new Team(TeamCode.RED, new int[]{0,1}, new int[]{1,1});
        teams[TeamCode.GREEN.getCode()] = new Team(TeamCode.GREEN, new int[]{1,0}, new int[]{0,-1});
        teams[TeamCode.BLUE.getCode()] = new Team(TeamCode.BLUE, new int[]{-1,-1}, new int[]{-1,0});
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board.length; j++){
                board[i][j] = -1;
            }
        }
        setUpTeams();
    }

    public void setTeamCode(int code){
        own_team = teams[code];
        setUpOwnTeam();
    }

    public boolean hasSetTeamCode(){
        return own_team != null;
    }

    public void update(Move receiveMove) {
        int moved_team_code = board[receiveMove.x][receiveMove.y];
        int[] movingDirection_Left = teams[moved_team_code].getmovingDirection_Left();
        int[] movingDirection_Right = teams[moved_team_code].getmovingDirection_Right();
        board[receiveMove.x][receiveMove.y] = -1;
        int[] newPieceLeft = new int[]{receiveMove.x + movingDirection_Left[0], receiveMove.y + movingDirection_Left[1]};
        int[] newPieceRight = new int[]{receiveMove.x + movingDirection_Right[0], receiveMove.y + movingDirection_Right[1]};
        board[newPieceLeft[0]][newPieceLeft[1]] = moved_team_code;
        board[newPieceRight[0]][newPieceRight[1]] = moved_team_code;
        if(moved_team_code == own_team.getTeamCode().getCode()){
            team_pieces.add(newPieceLeft);
            team_pieces.add(newPieceRight);
            for (int i =0; i < team_pieces.size(); i++) {
                int[] piece = team_pieces.get(i);
                if(piece[0] == receiveMove.x && piece[1] == receiveMove.y){
                    team_pieces.remove(piece);
                }
            }
        }
    }

    public int[] chooseRandomPiece(){
        Random rand = new Random();
        return team_pieces.get(rand.nextInt(team_pieces.size()));
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
            board[red][0] = TeamCode.RED.getCode();
        }
        for (int green = 0; green < NUMBER_OF_PIECES_PER_PLAYER; green++){
            board[green][green + 4] = TeamCode.GREEN.getCode();
        }
        for (int blue = 0; blue < NUMBER_OF_PIECES_PER_PLAYER; blue++){
            board[8][blue + 4] = TeamCode.BLUE.getCode();
        }
    }

    private void setUpOwnTeam(){
        for (int i = 0; i < board.length; i++){
            int[] boardRow = board[i];
            for (int j = 0; j < boardRow.length; j++){
                if(boardRow[j] == own_team.getTeamCode().getCode()){
                    team_pieces.add(new int[]{i, j});
                }
            }
        }
    }
}
