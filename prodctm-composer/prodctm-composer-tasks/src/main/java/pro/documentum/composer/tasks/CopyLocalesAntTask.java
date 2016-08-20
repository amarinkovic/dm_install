package pro.documentum.composer.tasks;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.tools.ant.BuildException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.Path;

import com.emc.ide.artifactmanager.uriconverter.IUrnFinderFactory;
import com.emc.ide.project.IDmProject;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class CopyLocalesAntTask extends AbstractAntTask {

    private static final String TASK_NAME = "pro.copyLocales";

    private String _projectName;

    private IDmProject _dmProject;

    private String _toDir;

    private boolean _require;

    public CopyLocalesAntTask() {
        super();
    }

    public void setProject(final String name) {
        trace("Parameter 'project' is '" + name + "'");
        _projectName = name;
    }

    public void setToDir(final String toDir) {
        trace("Parameter 'todir' is '" + toDir + "'");
        _toDir = toDir;
    }

    public void setRequire(final String require) {
        trace("Parameter 'require' is '" + require + "'");
        _require = Boolean.valueOf(require);
    }

    public void execute() throws BuildException {
        setTaskName(TASK_NAME);
        info("Copying locales folder from project '" + _projectName + "'");
        try {
            validateArgs();
            copyLocales();
        } catch (BuildException be) {
            throw be;
        } catch (Throwable e) {
            throw new BuildException(e);
        } finally {
            IUrnFinderFactory.INSTANCE.disposeFinderMap();
        }
    }

    private void copyLocales() throws BuildException {
        try {
            Path locales = new Path("locales");
            IFolder folder = _dmProject.getEclipseProject().getFolder(locales);
            if (!folder.exists()) {
                if (_require) {
                    throw new FileNotFoundException();
                }
                info("Project " + _projectName
                        + " does not contain locales folder");
                return;
            }
            copyDirectory(folder.getLocation().toFile(), new File(_toDir));
            info("Locales folder copied successfully from project '"
                    + _projectName + "'");
        } catch (FileNotFoundException ex) {
            throw new BuildException(ex);
        }
    }

    private void validateArgs() throws BuildException {
        validateParameterNotEmpty("project", _projectName);
        _dmProject = validateIsDocumentumProject(_projectName);
        validateParameterNotEmpty("todir", _toDir);
        validateDirectoryExists(_toDir);
    }

}
