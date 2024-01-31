package net.lenni0451.gradlecachedeleter.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {

    public static File[] list(final File file) {
        File[] files = file.listFiles();
        if (files == null) return new File[0];
        return files;
    }

    public static File[] list(final File[] files) {
        List<File> result = new ArrayList<>();
        for (File file : files) Collections.addAll(result, list(file));
        return result.toArray(new File[0]);
    }

    public static File[] findPrefixed(final File file, final String prefix) {
        File[] files = list(file);
        List<File> result = new ArrayList<>();
        for (File f : files) {
            if (f.getName().startsWith(prefix)) result.add(f);
        }
        return result.toArray(new File[0]);
    }

    public static boolean delete(final File file) {
        if (file.isDirectory()) {
            File[] files = list(file);
            boolean success = true;
            for (File f : files) {
                if (!delete(f)) success = false;
            }
            if (!success) return false;
        }
        return file.delete();
    }

}
