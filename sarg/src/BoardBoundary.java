public class BoardBoundary {
    private int[][] points;

    public BoardBoundary(int[][] boundaryPoints){
        this.points = boundaryPoints;
    }

    public boolean contains(int x, int y) {
        int i;
        int j;

        boolean result = false;
        for (i = 0, j = points.length - 1; i < points.length; j = i++) {
            if ((points[i][1] > y) != (points[j][1] > y) &&
                    (x < (points[j][0] - points[i][0]) * (y - points[i][1]) / (points[j][1]-points[i][1]) + points[i][0])) {
                result = !result;
            }
        }
        return result;
    }
}