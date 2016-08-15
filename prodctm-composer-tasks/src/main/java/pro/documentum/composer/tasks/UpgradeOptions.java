package pro.documentum.composer.tasks;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class UpgradeOptions {

    private List<Artifact> _artifacts = new ArrayList<Artifact>();

    public UpgradeOptions() {
        super();
    }

    public void addArtifact(final Artifact artifact) {
        _artifacts.add(artifact);
    }

    public List<Artifact> getArtifacts() {
        return _artifacts;
    }

}
