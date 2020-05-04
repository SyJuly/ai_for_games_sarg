public class TeamBlue extends Team {

    public TeamBlue() {
        super();
        this.code = TeamCode.BLUE;
        this.movingDirection_Left = new int[]{-1,-1};
        this.movingDirection_Right = new int[]{-1,0};
    }
}
