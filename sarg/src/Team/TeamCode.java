package Team;

public enum TeamCode {
    RED(0),
    GREEN(1),
    BLUE(2);

    private final int code;

    TeamCode(final int code) {
        this.code = code;
    }

    public int getCode() { return code; }
}
