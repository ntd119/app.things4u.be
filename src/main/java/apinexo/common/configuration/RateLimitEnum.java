package apinexo.common.configuration;

public enum RateLimitEnum {
    SECOND(1), MINUTE(60), HOUR(3600), DAY(86400);

    private final long seconds;

    RateLimitEnum(long seconds) {
        this.seconds = seconds;
    }

    public long toSeconds() {
        return seconds;
    }

    public long toMillis() {
        return seconds * 1000;
    }
}
