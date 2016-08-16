package pro.documentum.composer.tasks;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.tools.ant.BuildException;
import org.eclipse.core.resources.IFile;

import com.emc.ide.artifactmanager.uriconverter.IUrnFinderFactory;
import com.emc.ide.project.IDmProject;
import com.emc.ide.util.DmProjectUtils;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class CopyDarAntTask extends AbstractAntTask {

    private static final String TASK_NAME = "pro.copyDar";

    private String _projectName;

    private IDmProject _dmProject;

    private String _toDir;

    public CopyDarAntTask() {
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

    public void execute() throws BuildException {
        setTaskName(TASK_NAME);
        info("Copying dar file from project '" + _projectName + "'");
        try {
            validateArgs();
            copyDar();
        } catch (BuildException be) {
            throw be;
        } catch (Throwable e) {
            throw new BuildException(e);
        } finally {
            IUrnFinderFactory.INSTANCE.disposeFinderMap();
        }
        info("Dar file copied successfully from project '" + _projectName + "'");
    }

    private void copyDar() throws BuildException {
        try {
            IFile dar = DmProjectUtils.findDar(_dmProject.getEclipseProject());
            copyFile(dar.getLocation().toFile(), new File(_toDir));
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
