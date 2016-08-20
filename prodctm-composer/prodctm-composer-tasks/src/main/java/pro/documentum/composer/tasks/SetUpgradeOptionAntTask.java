package pro.documentum.composer.tasks;

import java.text.MessageFormat;

import org.apache.tools.ant.BuildException;
import org.eclipse.core.runtime.CoreException;

import com.emc.ide.artifact.dardef.model.dardef.UpgradeOptionValues;
import com.emc.ide.artifactmanager.uriconverter.IUrnFinderFactory;
import com.emc.ide.project.IDmProject;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class SetUpgradeOptionAntTask extends AbstractArtifactAntTask {

    private static final String TASK_NAME = "pro.setUpgradeOption";

    private String _projectName;

    private IDmProject _dmProject;

    private Artifacts _artifacts;

    public SetUpgradeOptionAntTask() {
        super();
    }

    public void setProject(final String name) {
        trace("Parameter 'project' is '" + name + "'");
        _projectName = name;
    }

    public void addArtifacts(final Artifacts options) {
        _artifacts = options;
    }

    public void execute() throws BuildException {
        setTaskName(TASK_NAME);
        info("Setting installation parameters for project '" + _projectName
                + "'");
        try {
            validateArgs();
            setUpgradeOption();
        } catch (BuildException be) {
            throw be;
        } catch (Throwable e) {
            throw new BuildException(e);
        } finally {
            IUrnFinderFactory.INSTANCE.disposeFinderMap();
        }
        info("Installation parameters for project '" + _projectName
                + "' were set successfully");
    }

    private void setUpgradeOption() throws CoreException {
        setUpgradeOptions(_dmProject, _artifacts);
    }

    private void validateArgs() throws BuildException {
        validateParameterNotEmpty("project", _projectName);
        _dmProject = validateIsDocumentumProject(_projectName);

        if (_artifacts == null || _artifacts.getArtifacts().isEmpty()) {
            trace("No artifacts options specified");
        }

        for (Artifact artifact : _artifacts.getArtifacts()) {
            validateParameterNotEmpty("name", artifact.getName());
            validateParameterNotEmpty("category", artifact.getCategory());
            validateParameterNotEmpty("upgradeOption",
                    artifact.getUpgradeOption());

            UpgradeOptionValues values = UpgradeOptionValues.getByName(artifact
                    .getUpgradeOption());
            if (values != null) {
                continue;
            }
            throw new BuildException(MessageFormat.format(
                    "Invalid upgrade option {0}", artifact.getUpgradeOption()));
        }
    }

}
