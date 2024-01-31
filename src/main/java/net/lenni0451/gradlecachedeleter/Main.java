package net.lenni0451.gradlecachedeleter;

import javax.swing.*;
import java.io.File;

public class Main {

    public static final File GRADLE_USER_HOME = new File(System.getProperty("user.home"), ".gradle");
    public static final File GRADLE_CACHES = new File(GRADLE_USER_HOME, "caches");
    public static final File GRADLE_MODULES = new File(GRADLE_CACHES, "modules-2");

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable t) {
            t.printStackTrace();
        }

        new GUI();
    }

}
