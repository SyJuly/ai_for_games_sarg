import java.util.List;

public class BoardKonfiguration {
    public Team[] teams;
    public Token[][] board;

    public BoardKonfiguration(Team[] teams, Token[][] board, int initialNumOfTokensPerPlayer){
        this.teams = teams;
        this.board = board;
        //setup start konfiguration
        setUpTokens(board, initialNumOfTokensPerPlayer);
        setUpTeamTokens(board);
    }

    public BoardKonfiguration(Team[] teams, Token[][] board){
        this.teams = teams;
        this.board = board;
    }

    public boolean areMoreTurnsPossible(int teamCode){
        return teams[teamCode].belongingTokens.isEmpty();
    }

    public List<Token> getCurrentTokensOfTeam(int teamCode){
        return teams[teamCode].belongingTokens;
    }

    public void addTokenToTeamList(Token token){
        if(token == null){
            return;
        }
        teams[token.teamCode].belongingTokens.add(token);
    }

    public void removeTokenFromTeamList(Token token){
        if(token == null){
            return;
        }
        teams[token.teamCode].belongingTokens.remove(token);
    }

    private void setUpTokens(Token[][] board, int initialNumOfTokensPerPlayer){
        for (int red = 0; red < initialNumOfTokensPerPlayer; red++){
            Token token = new Token(red, 0, TeamCode.RED.getCode());
            board[token.x][token.y] = token;
        }
        for (int green = 0; green < initialNumOfTokensPerPlayer; green++){
            Token token = new Token(green, green + 4, TeamCode.GREEN.getCode());
            board[token.x][token.y] = token;
        }
        for (int blue = 0; blue < initialNumOfTokensPerPlayer; blue++){
            Token token = new Token(8, blue + 4, TeamCode.BLUE.getCode());
            board[token.x][token.y] = token;
        }
    }

    private void setUpTeamTokens(Token[][] board){
        for (int x = 0; x < board.length; x++) {
            Token[] boardRow = board[x];
            for (int y = 0; y < boardRow.length; y++) {
                if (boardRow[y] != null) {
                    Token token = boardRow[y];
                    teams[token.teamCode].belongingTokens.add(token);
                }
            }
        }
    }

    public BoardKonfiguration copyConfig() {
        Token[][] copiedBoard = new Token[board.length][board[0].length];
        Team[] copiedTeams = new Team[teams.length];
        for (int j = 0; j < teams.length; j++) {
            Team team = teams[j];
            Team copiedTeam = new Team(team.getTeamCode(), team.getmovingDirection_Left(), team.getmovingDirection_Right());
            copiedTeams[j] = copiedTeam;
        }
        for(int i=0; i<board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                Token token = board[i][j];
                if(token == null){
                    continue;
                }
                Token copiedToken = new Token(token.x, token.y, token.teamCode);
                copiedBoard[i][j] = copiedToken;
                copiedTeams[token.teamCode].belongingTokens.add(token);
            }
        }
        return new BoardKonfiguration(copiedTeams, copiedBoard);
    }
}
