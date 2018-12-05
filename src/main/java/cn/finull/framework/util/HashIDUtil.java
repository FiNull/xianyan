package cn.finull.framework.util;

import org.hashids.Hashids;

public final class HashIDUtil {

    private final static String SALT = "12fdnu34@#$%^&*enkuhfd&*(_+)";

    private static final Hashids HASHIDS = new Hashids(SALT);

    public static String encode(long code) {
        return HASHIDS.encode(code);
    }

    public static long decode(String code) {
        return HASHIDS.decode(code)[0];
    }
}
