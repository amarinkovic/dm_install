package pro.documentum.composer.tasks;

import java.util.HashMap;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.eclipse.core.runtime.CoreException;

import com.emc.ide.artifactmanager.uriconverter.IUrnFinderFactory;
import com.emc.ide.project.IDmProject;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class SetUpgradeOptionsAntTask extends AbstractArtifactAntTask {

    private static final String TASK_NAME = "pro.setUpgradeOptions";

    private String _file;

    private String _delimiter = ":";

    public SetUpgradeOptionsAntTask() {
        super();
    }

    public void setFile(final String file) {
        trace("Parameter 'file' is '" + file + "'");
        _file = file;
    }

    public void setDelimiter(final String delimiter) {
        trace("Parameter 'delimiter' is '" + delimiter + "'");
        _delimiter = delimiter;
    }

    public void execute() throws BuildException {
        setTaskName(TASK_NAME);
        info("Setting installation parameters from file '" + _file + "'");
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
        info("Installation parameters from file '" + _file
                + "' were set successfully");
    }

    private Map<String, Artifacts> readFile() throws BuildException {
        Map<String, Artifacts> result = new HashMap<String, Artifacts>();
        for (String line : readLines(_file)) {
            String[] parts = line.split(_delimiter);
            String projectName = parts[0];
            Artifact artifact = new Artifact();
            artifact.setName(parts[1]);
            artifact.setCategory(parts[2]);
            artifact.setUpgradeOption(parts[3]);
            if (parts.length > 4) {
                artifact.setIgnoreReadOnly(parts[4]);
            }
            Artifacts options = result.get(projectName);
            if (options == null) {
                options = new Artifacts();
                result.put(projectName, options);
            }
            options.addArtifact(artifact);
        }
        return result;
    }

    private void setUpgradeOption() throws CoreException {
        Map<String, Artifacts> options = readFile();
        for (Map.Entry<String, Artifacts> e : options.entrySet()) {
            String projectName = e.getKey();
            Artifacts artifacts = e.getValue();
            IDmProject project = validateIsDocumentumProject(projectName);
            setUpgradeOptions(project, artifacts);
        }
    }

    private void validateArgs() throws BuildException {
        validateParameterNotEmpty("file", _file);
        validateFileExists(_file);
    }

}
