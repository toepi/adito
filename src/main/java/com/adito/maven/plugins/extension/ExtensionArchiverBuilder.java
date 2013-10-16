package com.adito.maven.plugins.extension;

import com.adito.extension.ExtensionBundle;
import com.adito.extension.PluginType;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.JAXB;
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

    private static boolean isDirectory(final File file) {
        return file != null && file.exists() && file.isDirectory();
    }
    private final String extensionName;
    private final MavenArchiver archiver;
    private final Set<String> classpathEntries;
    private final String outputDirectory;
    private ExtensionBundle extension;

    public ExtensionArchiverBuilder(final String extensionName,
            final JarArchiver archiver, final String outputDirectory) {
        this.extensionName = extensionName;
        this.archiver = new MavenArchiver();
        this.archiver.setArchiver(archiver);
        this.outputDirectory = outputDirectory;
        this.archiver.setOutputFile(createOutputFile(outputDirectory, extensionName));
        classpathEntries = new HashSet<String>();
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

    public ExtensionArchiverBuilder addExtensionClasspathFile(final Collection<Artifact> artifacts,
            final String targetDirectory) throws ArchiverException {
        for (Artifact artifact : artifacts) {
            if (!artifact.isOptional()) {
                addExtensionClasspathFile(artifact.getFile(), targetDirectory);
            }
        }
        return this;
    }

    public ExtensionArchiverBuilder addExtensionClasspathFile(final File file,
            final String targetDirectory) throws ArchiverException {
        final ExtensionArchiverBuilder result = addExtensionFile(file, targetDirectory);
        addToClassPath(file, targetDirectory);
        return result;
    }

    public ExtensionArchiverBuilder addExtensionFile(final File file) throws ArchiverException {
        return addExtensionFile(file, null);
    }

    public ExtensionArchiverBuilder addExtensionFile(final File file,
            final String targetDirectory) throws ArchiverException {
        if (isFile(file)) {
            archiver.getArchiver()
                    .addFile(file, asInExtensionFileName(file, targetDirectory));
        }
        return this;
    }

    private void addToClassPath(final File file, final String targetDirectory) {
        final StringBuilder sb = new StringBuilder();
        if (targetDirectory != null) {
            sb.append(targetDirectory).append('/');
        }
        sb.append(file.getName());
        classpathEntries.add(sb.toString());
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
        File file = null;
        try {
            if (extension != null) {
                file = writeClassPath();
                addExtensionFile(file);
            }
            archiver.createArchive(project, cfg);
            return archiver.getArchiver().getDestFile();
        } finally {
            if (file != null) {
                file.delete();
            }
        }
    }

    private File writeClassPath() throws IOException {
        final PluginType plugin = extension.getExtension().getPlugin();
        classpathEntries.removeAll(plugin.getClasspath());
        plugin.getClasspath().addAll(classpathEntries);
        final File file = new File(outputDirectory, "extension.xml");
        JAXB.marshal(extension, file);
        return file;
    }

    ExtensionArchiverBuilder addExtensionBundle(final ExtensionBundle extension) {
        this.extension = extension;
        return this;
    }
}
