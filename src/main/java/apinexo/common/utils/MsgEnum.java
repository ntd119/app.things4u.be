package apinexo.common.utils;

public enum MsgEnum {
    REQUIRED("The %s is required"), NOT_AVAILABLE("The %s is not available"), INVALID("The %s is invalid"),
    INCORRECT_YYYYMMDD("Incorrect %s format, should be YYYY-MM-DD"),
    INCORRECT_YYYYMMDDHHMM("Incorrect %s format, should be YYYY-MM-DDTHH:MM"),
    INCORRECT_YYYYMMDDHHMMSS("Incorrect %s format, should be yyyy-MM-ddTHH:mm:ss"),
    INCORRECT_YYYYMM("Incorrect %s format, should be YYYY-MM"), INCORRECT_MMDD("Incorrect %s format, should be MM-DD"),
    INCORRECT_HHMM("Incorrect %s format, should be HH:MM"), INCORRECT_HHHMM("Incorrect %s format, should be HHhMM"),
    CANNOT_BEFORE_DATE("The %s cannot be entered before %s"), MUST_AFTER_DATE("The %s must be after %s"),
    LESS_EQUAL("The %s must be less than or equal to %s"), GREATER_EQUAL("The %s must be greater than or equal to %s"),
    RANGE_VALUE("The %s has a value from %s to %s");

    private final String message;

    MsgEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
