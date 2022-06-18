package org.example;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Named
@Singleton
public class BannerProvider {

    private static final String[] BANNER = { "", "  .   ____          _            __ _ _",
            " /\\\\ / ___'_ __ _ _(_)_ __  __ _ \\ \\ \\ \\", "( ( )\\___ | '_ | '_| | '_ \\/ _` | \\ \\ \\ \\",
            " \\\\/  ___)| |_)| | | | | || (_| |  ) ) ) )", "  '  |____| .__|_| |_|_| |_\\__, | / / / /",
            " =========|_|==============|___/=/_/_/_/" };

    private static final String SPRING_BOOT = " :: Spring Boot :: ";
    private static final String TEMPLATE = " :: %s :: ";
    private static final int STRAP_LINE_SIZE = 42;

    public void generateBanner(Log log, MavenProject project, CustomProperties customProperties) {
        StringBuilder sb = new StringBuilder();
        readCustomBanner(log, project, customProperties, sb);

        String springBootLine = getSpringBootLine(project);
        sb.append(springBootLine);

        String projectLine = getProjectLine(project);
        sb.append(projectLine);

        writeBanner(log, project, sb);
    }

    private void readCustomBanner(Log log, MavenProject project, CustomProperties customProperties, StringBuilder sb) {
        List<Resource> resources = project.getResources();
        boolean appendCustom = false;
        for (Resource resource : resources) {
            if (resource.getDirectory() != null) {
                String path = resource.getDirectory();
                File bannerFile = new File(path, customProperties.getFileName());
                log.info("Read Banner from " + bannerFile.getAbsolutePath());
                if (bannerFile.exists()) {
                    try {
                        String oldFile = FileUtils.readFileToString(bannerFile, "utf-8");
                        sb.append(oldFile);
                        sb.append("\r\n");
                        appendCustom = true;
                        break;
                    } catch (IOException e) {
                        log.error("Cannot read custom banner.", e);
                    }
                }
            }
        }
        if (!appendCustom) {
            //TODO based on type
            for (String line : BANNER) {
                sb.append(line);
                sb.append("\r\n");
            }
        }
    }

    private void writeBanner(Log log, MavenProject project, StringBuilder sb) {
        File bannerResult = new File(project.getFile().getParentFile(), "target/classes/banner.txt");
        log.info("Write Banner to" + bannerResult.getAbsolutePath());
        FileUtils.deleteQuietly(bannerResult);
        try {
            FileUtils.write(bannerResult, sb.toString(), "utf-8");
        } catch (IOException e) {
            log.error("Cannot writer banner.", e);
        }
    }

    private String getSpringBootLine(MavenProject project) {
        List<Dependency> dependencies = project.getDependencies();
        String bootVersion = "";
        for (Dependency dependency : dependencies) {
            String artifactId1 = dependency.getArtifactId();
            if (artifactId1.contains("spring-boot-starter")) {
                bootVersion = dependency.getVersion();
            }
        }
        return parseToVersionLine(SPRING_BOOT, bootVersion);
    }

    private String getProjectLine(MavenProject project) {
        Artifact artifact = project.getArtifact();
        String artifactId = artifact.getArtifactId();
        String version = artifact.getVersion();
        return parseToVersionLine(String.format(TEMPLATE, artifactId), version);
    }

    private String parseToVersionLine(String title, String version) {
        version = (version != null) ? " (v" + version + ")" : "";
        StringBuilder padding = new StringBuilder();
        padding.append(title);
        while (padding.length() < STRAP_LINE_SIZE - (version.length() + title.length())) {
            padding.append(" ");
        }
        padding.append(version);
        padding.append("\r\n");
        return padding.toString();
    }

}
