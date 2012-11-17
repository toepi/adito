package com.adito.maven.plugins.extension;

import java.io.File;
import java.io.IOException;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;

/**
 * @goal adito-extension
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class AditoExtensionMojo extends AbstractMojo {

    /**
     * The directory for the generated extension.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    protected String outputDirectory;
    /**
     * The directory containing generated classes.
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private File classesDirectory;
    /**
     * Name of the extention that adito uses for display purpose. It should be
     * one line text.
     *
     * @parameter expression="${project.artifactId}"
     * @required
     * @readonly
     */
    private String extensionName;
    /**
     * Single directory for web files to include in the extention.
     *
     * @parameter expression="${basedir}/src/main/webapp"
     * @required
     */
    private File warSourceDirectory;
    /**
     * Single directory for extra files to include in the extention.
     *
     * @parameter expression="${basedir}/src/main/extension"
     * @required
     */
    private File extensionSourceDirectory;
    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
    /**
     * The maven archive configuration to use.
     *
     * @parameter
     */
    private MavenArchiveConfiguration archive;
    /**
     * Used to create .jar archive.
     *
     * @component role="org.codehaus.plexus.archiver.Archiver" role-hint="jar"
     * @required
     */
    private JarArchiver jarArchiver;
    /**
     * Used to create .zip archive.
     *
     * @component role="org.codehaus.plexus.archiver.Archiver" role-hint="jar"
     * @required
     */
    private JarArchiver extensionJarArchiver;
    /**
     * Create extension as jar and put as into private.
     *
     * @parameter default-value="true"
     */
    private boolean createJar;
    /**
     * @component
     */
    private MavenProjectHelper projectHelper;

    public AditoExtensionMojo() {
        this.archive = new MavenArchiveConfiguration();
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            performPackaging();
        } catch (ArchiverException ex) {
            throw new MojoExecutionException(String.format("Error assembling extension: %s", ex.getMessage()), ex);
        } catch (ManifestException ex) {
            throw new MojoExecutionException(String.format("Error assembling extension: %s", ex.getMessage()), ex);
        } catch (IOException ex) {
            throw new MojoExecutionException(String.format("Error assembling extension: %s", ex.getMessage()), ex);
        } catch (DependencyResolutionRequiredException ex) {
            throw new MojoExecutionException(String.format("Error assembling extension: %s", ex.getMessage()), ex);
        }
    }

    private void performPackaging() throws ArchiverException, ManifestException,
            IOException, DependencyResolutionRequiredException {
        final File jarFile;
        if (createJar) {
            jarFile = createJarFile();
            projectHelper.attachArtifact(project, "jar", jarFile);
        } else {
            jarFile = null;
        }
        final File extension = new ExtensionArchiverBuilder(extensionName, extensionJarArchiver, outputDirectory)
                .addExtensionDirectory(getExtensionSourceDirectory())
                .addExtensionFile(jarFile, "private")
                .addExtensionFile(project.getCompileArtifacts(), "private")
                .addExtensionDirectory(getWebappDirectory(), "webapp")
                .createArchive(project, archive);
        projectHelper.attachArtifact(project, "zip", extension);
    }

    private File createJarFile() throws IOException, ManifestException,
            DependencyResolutionRequiredException, ArchiverException {
        final File result = getOutputFile("jar");
        final MavenArchiver archiver = new MavenArchiver();
        archiver.setArchiver(jarArchiver);
        archiver.setOutputFile(result);
        if (getClassesDirectory().exists()) {
            jarArchiver.addDirectory(getClassesDirectory());
        }
        archiver.createArchive(project, archive);
        return result;
    }

    private File getOutputFile(String extension) {
        return new File(new File(outputDirectory), String.format("%s.%s", extensionName, extension));
    }

    private File getWebappDirectory() {
        return warSourceDirectory;
    }

    private File getClassesDirectory() {
        return classesDirectory;
    }

    private File getExtensionSourceDirectory() {
        return extensionSourceDirectory;
    }
}
