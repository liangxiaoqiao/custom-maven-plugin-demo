#### A banner maven plugin
1. Introduction
1. Create maven project 
1. A simple mojo
1. Execution
1. Banner maven plugin
    1. LifecyclePhase
    1. Parameters
        1. Config parameters in a project
        2. Supported parameter types
    1. Usage


### 1. Introduction
##### What is a Mojo? 
>A mojo is a Maven plain Old Java Object. Each mojo is an executable goal in Maven, and a plugin is a distribution of one or more related mojos.

##### Plugin Name:
- You will typically name your plugin **\<yourplugin>-maven-plugin.**
- Cannot use **maven-\<yourplugin>-plugin**

### 2. Create maven project
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>org.example</groupId>
  <packaging>maven-plugin</packaging>
  <artifactId>banner-maven-plugin</artifactId>
  <version>1.0</version>
  <name>banner-maven-plugin Maven Mojo</name>

  <properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.apache.maven</groupId>
      <artifactId>maven-plugin-api</artifactId>
      <version>3.6.1</version>
      <scope>provided</scope>
    </dependency>
    <dependency>
      <groupId>org.apache.maven.plugin-tools</groupId>
      <artifactId>maven-plugin-annotations</artifactId>
      <version>3.6.1</version>
      <scope>provided</scope>
    </dependency>
    <dependency>
      <groupId>org.apache.maven</groupId>
      <artifactId>maven-core</artifactId>
      <version>3.6.1</version>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-plugin-plugin</artifactId>
        <version>3.6.1</version>
      </plugin>
    </plugins>
  </build>
</project>
```
### 3. A simple mojo
```java
@Mojo( name = "sayhi")
public class GreetingMojo extends AbstractMojo
{
    public void execute() throws MojoExecutionException
    {
        getLog().info( "Hello, world." );
    }
}
```
- The class org.apache.maven.plugin.AbstractMojo provides most of the infrastructure required to implement a mojo except for the execute method.
- The annotation "@Mojo" is required and control how and when the mojo is executed.
- The execute method can throw two exceptions:
    - org.apache.maven.plugin.MojoExecutionException if an unexpected problem occurs. Throwing this exception causes a "BUILD ERROR" message to be displayed.
    - org.apache.maven.plugin.MojoFailureException if an expected problem (such as a compilation failure) occurs. Throwing this exception causes a "BUILD FAILURE" message to be displayed.
- The getLog method (defined in AbstractMojo) returns a log4j-like logger object which allows plugins to create messages at levels of "debug", "info", "warn", and "error". This logger is the accepted means to display information to the user. Please have a look at the section Retrieving the Mojo Logger for a hint on its proper usage.


### 4. Execution
- mvn groupId:artifactId:version:goal
> For example, to run the simple mojo in the sample plugin, you would enter "mvn sample.plugin:hello-maven-plugin:1.0-SNAPSHOT:sayhi" on the command line.

### 5. Banner maven plugin
```java
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
}
```
#### 1. LifecyclePhase
```java
    VALIDATE( "validate" ),
    INITIALIZE( "initialize" ),
    GENERATE_SOURCES( "generate-sources" ),
    PROCESS_SOURCES( "process-sources" ),
    GENERATE_RESOURCES( "generate-resources" ),
    PROCESS_RESOURCES( "process-resources" ),
    COMPILE( "compile" ),
    PROCESS_CLASSES( "process-classes" ),
    GENERATE_TEST_SOURCES( "generate-test-sources" ),
    PROCESS_TEST_SOURCES( "process-test-sources" ),
    GENERATE_TEST_RESOURCES( "generate-test-resources" ),
    PROCESS_TEST_RESOURCES( "process-test-resources" ),
    TEST_COMPILE( "test-compile" ),
    PROCESS_TEST_CLASSES( "process-test-classes" ),
    TEST( "test" ),
    PREPARE_PACKAGE( "prepare-package" ),
    PACKAGE( "package" ),
    PRE_INTEGRATION_TEST( "pre-integration-test" ),
    INTEGRATION_TEST( "integration-test" ),
    POST_INTEGRATION_TEST( "post-integration-test" ),
    VERIFY( "verify" ),
    INSTALL( "install" ),
    DEPLOY( "deploy" ),

    PRE_CLEAN( "pre-clean" ),
    CLEAN( "clean" ),
    POST_CLEAN( "post-clean" ),

    PRE_SITE( "pre-site" ),
    SITE( "site" ),
    POST_SITE( "post-site" ),
    SITE_DEPLOY( "site-deploy" ),
    NONE( "" );
```
#### 2. Parameters
1. Config parameters in a project
    ```java
    @Parameter(property = "fileName", defaultValue = "banner.txt")
    private String fileName;
    ```
1. Supported parameter types
> Boolean, Integer, Floating, Dates, Files and Directories, URLs, Plain Text, Enums
> Arrays, Collections, Maps, Properties, Other Object

#### 3. Usages
```xml
<plugin>
    <groupId>org.example</groupId>
    <artifactId>banner-maven-plugin</artifactId>
    <version>1.0</version>
    <executions>
        <execution>
            <goals>
                <goal>banner</goal>
            </goals>
        </execution>
    </executions>
</plugin
```