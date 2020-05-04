public class TeamGreen extends Team {

    public TeamGreen() {
        super();
        this.code = TeamCode.GREEN;
        this.movingDirection_Left =  new int[]{1,0};
        this.movingDirection_Right = new int[]{0,-1};
    }
}
