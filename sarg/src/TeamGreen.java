public class TeamGreen extends Team {

    protected static TeamCode code = TeamCode.GREEN;

    public TeamGreen() {
        super();
        this.movingDirection_Left =  new int[]{1,0};
        this.movingDirection_Right = new int[]{0,-1};
        this.lineBorder = new int[][]{{0,4}, {8,4}};
    }

    @Override
    public double calculateDistanceToClosestWinningBorder(int x, int y) {
        double[] distances = new double[3];

        distances[0] = x;
        distances[1] = Math.abs(8 - y);
        distances[2] = getDistanceFromPointToLine(x, y, lineBorder);

        return Math.min(distances[0], Math.min(distances[1], distances[2]));
    }

    @Override
    public TeamCode getTeamCode() {
        return code;
    }
}
