package nl.minjus.nfi.dt.jhashtools.utils;

import java.util.Locale;

public final class OsUtils
{
    private static String OS = null;

    public static String getOsName() {
        if (OS == null) {
            OS = System.getProperty("os.name");
        }
        return OS;
    }

    public static boolean isWindows() {
        return getOsName().toLowerCase(Locale.ROOT).startsWith("windows");
    }

    public static boolean isUnix() {
        return getOsName().toLowerCase(Locale.ROOT).startsWith("linux");
    }

    public static boolean isMac() {
        return getOsName().toLowerCase(Locale.ROOT).startsWith("mac");
    }
}
