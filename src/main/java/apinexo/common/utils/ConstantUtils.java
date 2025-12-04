package apinexo.common.utils;

public class ConstantUtils {
    public static final String DATE_FORMAT_YYYYMMDDTHHMMSSZ = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String DATE_FORMAT_YYYYMMDDTHH0000Z = "yyyy-MM-dd'T'HH:00:00'Z'";
    public static final String DATE_FORMAT_YYYYMM = "yyyy-MM";
    public static final String TIME_ZONE_DEFAULT = "America/New_York";
    public static final String TIME_ZONE_UCT = "UCT";

    public static class Pattern {
        public static final String PAGE_URL = "/(\\d+_p)/";
    }

    public static class DateFormat {
        public static final String YYYYMMDDTHHMMssSSS = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        public static final String YYYYMMDDHHMMssSSS = "yyyyMMddHHmmssSSS";
    }
}
