package com.adito.maven.plugins.extension;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;

/**
 *
 * @author sebastian
 */
class ExtensionArchiverBuilder {

    private static boolean isFile(final File file) {
        return file != null && file.exists() && file.isFile();
    }

    private boolean isDirectory(final File file) {
        return file != null && file.exists() && file.isDirectory();
    }
    private final String extensionName;
    private final MavenArchiver archiver;

    public ExtensionArchiverBuilder(final String extensionName,
            final JarArchiver archiver, final String outputDirectory) {
        this.extensionName = extensionName;
        this.archiver = new MavenArchiver();
        this.archiver.setArchiver(archiver);
        this.archiver.setOutputFile(createOutputFile(outputDirectory, extensionName));
    }

    private File createOutputFile(final String outputDirectory,
            final String extensionName) {
        return new File(outputDirectory, String.format("%s.zip", extensionName));
    }

    public ExtensionArchiverBuilder addExtensionDirectory(final File dir)
            throws ArchiverException {
        return addExtensionDirectory(dir, null);
    }

    public ExtensionArchiverBuilder addExtensionDirectory(final File dir,
            final String targetDirectory) throws ArchiverException {
        if (isDirectory(dir)) {
            archiver.getArchiver()
                    .addDirectory(dir, asExtensionDirectoryName(targetDirectory));
        }
        return this;
    }

    public ExtensionArchiverBuilder addExtensionFile(final Collection<Artifact> artifacts,
            final String targetDirectory) throws ArchiverException {
        for (Artifact artifact : artifacts) {
            if (!artifact.isOptional()) {
                addExtensionFile(artifact.getFile(), targetDirectory);
            }
        }
        return this;
    }

    public ExtensionArchiverBuilder addExtensionFile(final File file,
            final String targetDirectory) throws ArchiverException {
        if (isFile(file)) {
            archiver.getArchiver()
                    .addFile(file, asInExtensionFileName(file, targetDirectory));
        }
        return this;
    }

    private String asInExtensionFileName(final File file, final String inpaths) {
        return String.format("%s%s", asExtensionDirectoryName(inpaths), file.getName());
    }

    private String asExtensionDirectoryName(final String subpath) {
        final StringBuilder sb = new StringBuilder(extensionName);
        if (subpath != null) {
            sb.append('/').append(subpath);
        }
        return sb.append('/').toString();
    }

    public File createArchive(final MavenProject project,
            final MavenArchiveConfiguration cfg) throws ArchiverException,
            ManifestException, DependencyResolutionRequiredException, IOException {
        archiver.createArchive(project, cfg);
        return archiver.getArchiver().getDestFile();
    }
}
