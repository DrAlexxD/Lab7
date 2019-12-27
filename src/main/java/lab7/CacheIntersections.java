package lab7;

public class CacheIntersections {
    private String leftBorder, rightBorder;
    private long time;

    public CacheIntersections(String leftBorder, String rightBorder, long time) {
        this.leftBorder = leftBorder;
        this.rightBorder = rightBorder;
        this.time = time;
    }

    public String getLeftBorder() {
        return leftBorder;
    }

    public String getRightBorder() {
        return rightBorder;
    }

    public boolean isIntersect(String a) {
        int req = Integer.parseInt(a);
        int leftBorder = Integer.parseInt(leftBorder);
        int rightBorder = Integer.parseInt(rightBorder);
        return leftBorder <= req && req <= rightBorder;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long t) {
        time = t;
    }
}
