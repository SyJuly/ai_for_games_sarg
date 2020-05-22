package Logging;

public class LogDepthReport {
    public int[] depthLogging;
    public int[] depths;
    public int teamCode;

    public LogDepthReport(int[] depthLogging, int[] depths, int teamCode){
        this.depthLogging = depthLogging;
        this.depths = depths;
        this.teamCode = teamCode;
    }
}
