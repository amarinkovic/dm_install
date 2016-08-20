package pro.documentum.composer.tasks.filters;

import com.emc.ide.artifactmanager.artifact.IDmArtifact;

import pro.documentum.composer.tasks.Artifact;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class FilterArtifactByCategory extends AbstractDmArtifactFilter {

    public FilterArtifactByCategory(final Artifact option) {
        super(option);
    }

    @Override
    protected boolean doAcceptArtifact(final IDmArtifact artifact) {
        return categoryMatches(artifact);
    }

}
