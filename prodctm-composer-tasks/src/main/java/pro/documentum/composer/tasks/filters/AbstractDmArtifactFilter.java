package pro.documentum.composer.tasks.filters;

import com.emc.ide.artifactmanager.artifact.DmArtifactFilter;
import com.emc.ide.artifactmanager.artifact.IDmArtifact;
import com.emc.ide.logger.dbc.DBC;

import pro.documentum.composer.tasks.Artifact;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractDmArtifactFilter extends DmArtifactFilter {

    private static final String WILDCARD = "*";

    private final Artifact _option;

    protected AbstractDmArtifactFilter(final Artifact option) {
        _option = option;
    }

    @Override
    protected final boolean doAccept(final IDmArtifact artifact) {
        DBC.preCondition(artifact != null, "artifact must not be null");
        if (isDerived(artifact)) {
            return false;
        }
        if (!isIgnoreReadOnly()) {
            if (!artifact.isModifiable()) {
                return false;
            }
            if (isDocumentumArtifact(artifact)) {
                return false;
            }
        }
        return doAcceptArtifact(artifact);
    }

    protected abstract boolean doAcceptArtifact(final IDmArtifact artifact);

    protected final boolean isIgnoreReadOnly() {
        return _option.isIgnoreReadOnly();
    }

    protected final boolean isDocumentumArtifact(final IDmArtifact artifact) {
        return artifact.getName().startsWith("dm_")
                || artifact.getName().startsWith("dmc_");
    }

    protected final boolean nameMatches(final IDmArtifact artifact) {
        if (WILDCARD.equals(_option.getName())) {
            return true;
        }
        String name = artifact.getName();
        return name.equals(_option.getName());
    }

    protected final boolean categoryMatches(final IDmArtifact artifact) {
        if (WILDCARD.equals(_option.getCategory())) {
            return true;
        }
        String category = artifact.getCategory().getCategoryId();
        return category.equals(_option.getCategory());
    }

    protected final boolean isDerived(final IDmArtifact artifact) {
        return "artifact".equals(artifact.getFile().getFileExtension());
    }

}
