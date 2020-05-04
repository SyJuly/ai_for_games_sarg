public class TeamRed extends Team {
    public TeamRed() {
        super();
        this.code = TeamCode.RED;
        this.movingDirection_Left =  new int[]{0,1};
        this.movingDirection_Right = new int[]{1,1};
    }
}
