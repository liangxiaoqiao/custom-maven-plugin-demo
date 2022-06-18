package org.example;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import javax.inject.Inject;

@Mojo(name = "banner", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class BannerMavenMojo extends AbstractMojo {

    @Parameter(property = "fileName", defaultValue = "banner.txt")
    private String fileName;

    @Parameter(property = "project", readonly = true)
    private MavenProject project;

    @Inject
    private BannerProvider bannerProvider;


    public void execute() throws MojoExecutionException, MojoFailureException {
        bannerProvider.generateBanner(getLog(), project, prepareCustomProperties());
    }

    private CustomProperties prepareCustomProperties() {
        CustomProperties customProperties = new CustomProperties();
        customProperties.setFileName(fileName);
        return customProperties;
    }

}


class CustomProperties {
    private String fileName;
    private String type;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}