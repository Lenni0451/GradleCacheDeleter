package net.lenni0451.gradlecachedeleter.utils;

public class StringUtils {

    public static boolean matchesAll(final String[] search, final String... parts) {
        for (String s : search) {
            boolean found = false;
            for (String part : parts) {
                if (part.toLowerCase().contains(s)) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

}
