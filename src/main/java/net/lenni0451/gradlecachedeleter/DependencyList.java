package net.lenni0451.gradlecachedeleter;

import net.lenni0451.gradlecachedeleter.utils.FileUtils;

import java.io.File;
import java.util.*;

public class DependencyList {

    private final Map<String, Map<String, Map<String, List<File>>>> dependencies;

    public DependencyList() {
        this.dependencies = new HashMap<>();

        this.crawl();
    }

    public Map<String, Map<String, Map<String, List<File>>>> getDependencies() {
        return this.dependencies;
    }

    public void printTree() {
        for (Map.Entry<String, Map<String, Map<String, List<File>>>> pkg : this.dependencies.entrySet()) {
            System.out.println(pkg.getKey());
            for (Map.Entry<String, Map<String, List<File>>> name : pkg.getValue().entrySet()) {
                System.out.println("  " + name.getKey());
                for (Map.Entry<String, List<File>> version : name.getValue().entrySet()) {
                    System.out.println("    " + version.getKey());
                }
            }
        }
    }

    private void crawl() {
        File[] files = FileUtils.findPrefixed(Main.GRADLE_MODULES, "files-");
        File[] metadata = this.searchMetadata();

        this.crawl(files);
        this.crawl(metadata);
    }

    private File[] searchMetadata() {
        File[] metadata = FileUtils.findPrefixed(Main.GRADLE_MODULES, "metadata-");
        List<File> files = new ArrayList<>();
        for (File f : metadata) {
            File descriptors = new File(f, "descriptors");
            if (descriptors.exists()) files.add(descriptors);
        }
        return files.toArray(new File[0]);
    }

    private void crawl(final File[] files) {
        for (File packageFile : FileUtils.list(files)) {
            String pkg = packageFile.getName();
            for (File nameFile : FileUtils.list(packageFile)) {
                String name = nameFile.getName();
                for (File versionFile : FileUtils.list(nameFile)) {
                    String version = versionFile.getName();
                    this.dependencies
                            .computeIfAbsent(pkg, k -> new HashMap<>())
                            .computeIfAbsent(name, k -> new HashMap<>())
                            .computeIfAbsent(version, k -> new ArrayList<>())
                            .addAll(Arrays.asList(FileUtils.list(versionFile)));
                }
            }
        }
    }

}
