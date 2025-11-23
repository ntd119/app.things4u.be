package apinexo.common.utils;

public enum RateLimitEnum {

    SECOND(1000L),
    MINUTE(60_000L),
    HOUR(3_600_000L),
    DAY(86_400_000L);

    private final long millis;

    RateLimitEnum(long millis) {
        this.millis = millis;
    }

    public long toMillis() {
        return millis;
    }
}