package com.adito.maven.plugins.extension;

import com.jcabi.aether.Aether;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.JavaScopes;

/**
 *
 * @author toepi <toepi@users.noreply.github.com>
 */
public class ProjectDependencyResolver {

    private final Aether ather;
    private final MavenProject project;

    public ProjectDependencyResolver(final RepositorySystemSession session,
            final MavenProject project) {
        final File repo = session.getLocalRepository().getBasedir();
        this.project = project;
        ather = new Aether(project, repo);
    }

    public Collection<Artifact> getCompileArtifacts() throws DependencyResolutionException {
        final Collection<Artifact> deps = new HashSet<Artifact>();
        for (Artifact artifact : getProjectArtifact()) {
            deps.addAll(ather.resolve(artifact, JavaScopes.COMPILE));
        }
        return deps;
    }

    private Collection<Artifact> getProjectArtifact() {
        List<org.apache.maven.artifact.Artifact> projectDeps = project.getCompileArtifacts();
        final Collection<Artifact> result = new HashSet<Artifact>();
        for (org.apache.maven.artifact.Artifact projectDep : projectDeps) {
            if (!projectDep.isOptional()) {
                final DefaultArtifact artifact = new DefaultArtifact(
                        projectDep.getGroupId(),
                        projectDep.getArtifactId(),
                        projectDep.getType(),
                        projectDep.getVersion());
                result.add(artifact);
            }
        }
        return result;
    }
}
