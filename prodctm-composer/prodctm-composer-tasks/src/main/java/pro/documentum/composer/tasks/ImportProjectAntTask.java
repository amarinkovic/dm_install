package pro.documentum.composer.tasks;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.emc.ide.artifactmanager.uriconverter.IUrnFinderFactory;
import com.emc.ide.logger.dbc.DBC;
import com.emc.ide.project.IDmProjectFactory;
import com.emc.ide.project.IDmProjectFactoryCache;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class ImportProjectAntTask extends AbstractAntTask {

    private static final String TASK_NAME = "pro.importProject";

    private static final String PROJECT_FILE = ".project";

    private String _projectName;

    private String _projectLocation;

    private boolean _replace;

    private boolean _copy;

    public ImportProjectAntTask() {
        super();
    }

    public void setProject(final String name) {
        trace("Parameter 'project' is '" + name + "'");
        _projectName = name;
    }

    public void setLocation(final String location) {
        trace("Parameter 'location' is '" + location + "'");
        _projectLocation = location;
    }

    public void setReplace(final String replace) {
        trace("Parameter 'replace' is '" + replace + "'");
        _replace = Boolean.valueOf(replace);
    }

    public void setCopy(final String copy) {
        trace("Parameter 'copy' is '" + copy + "'");
        _copy = Boolean.valueOf(copy);
    }

    private void setAutoBuild(final boolean autoBuild) throws CoreException {
        IWorkspace theWorkspace = ResourcesPlugin.getWorkspace();
        IWorkspaceDescription description = theWorkspace.getDescription();
        if (description.isAutoBuilding() == autoBuild) {
            return;
        }
        description.setAutoBuilding(autoBuild);
        theWorkspace.setDescription(description);
    }

    public void execute() throws BuildException {
        setTaskName(TASK_NAME);
        info("Importing project '" + _projectName + "' from '"
                + _projectLocation + "'");
        try {
            setAutoBuild(false);
            validateArgs();
            importDocumentumProject();
        } catch (BuildException be) {
            throw be;
        } catch (Throwable e) {
            throw new BuildException(e);
        } finally {
            IUrnFinderFactory.INSTANCE.disposeFinderMap();
        }
        info("Project '" + _projectName + "' was successfully imported");
    }

    private void importDocumentumProject() throws BuildException, CoreException {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot workspaceRoot = workspace.getRoot();
        IProgressMonitor progressMonitor = new NullProgressMonitor();

        File projectFolder = new File(_projectLocation, _projectName);
        File projectProperties = new File(projectFolder.getPath(), ".project");
        if (!projectProperties.exists()) {
            throw new BuildException("Unable to locate .project file in "
                    + projectFolder.getAbsolutePath() + " directory");
        }

        createCoreProject(workspaceRoot, progressMonitor);

        IProject project = workspaceRoot.getProject(_projectName);
        if (project.exists()) {
            if (!project.isOpen()) {
                project.open(progressMonitor);
            }
            if (!_replace) {
                info("Project " + _projectName + " already exists");
                DBC.invariant(project.isOpen(), "Project being imported '"
                        + project.getName() + "' must not be closed");
                refreshProject(project, progressMonitor);
                return;
            }
            info("Project with name " + _projectName
                    + " already exists, deleting");
            removeProjectFromWorkspace(workspaceRoot, project, progressMonitor);
        }
        importProjectIntoWorkspace(workspace, _projectName, projectProperties,
                progressMonitor);
    }

    private void removeProjectFromWorkspace(final IWorkspaceRoot workspaceRoot,
            final IProject project, final IProgressMonitor progressMonitor)
        throws CoreException {
        boolean isLocal = isProjectInWorkspaceDirectory(workspaceRoot, project);
        if (IDmProjectFactory.INSTANCE instanceof IDmProjectFactoryCache) {
            ((IDmProjectFactoryCache) IDmProjectFactory.INSTANCE)
                    .removeProjectFromCache(project);
        }
        if (isLocal) {
            trace("Project " + _projectName + " is located inside workspace");
            project.close(progressMonitor);
            project.delete(true, true, progressMonitor);
        } else {
            project.close(progressMonitor);
            project.delete(false, true, progressMonitor);
        }
    }

    private IProject importProjectIntoWorkspace(final IWorkspace workspace,
            final String projectName, final File projectProperties,
            final IProgressMonitor progressMonitor) throws CoreException {
        File properties = projectProperties;
        if (_copy) {
            File root = workspace.getRoot().getLocation().toFile();
            File projectDir = projectProperties.getParentFile();
            File newProjectDir = new File(root, projectName);
            copyDirectory(projectDir, newProjectDir);
            properties = new File(newProjectDir, PROJECT_FILE);
        }
        IProject project = importProject(projectName, properties, workspace,
                progressMonitor);
        refreshProject(project, progressMonitor);
        return project;
    }

    private void refreshProject(final IProject project,
            final IProgressMonitor progressMonitor) throws CoreException {
        project.refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
    }

    private void validateArgs() throws BuildException {
        validateParameterNotEmpty("project", _projectName);
        validateParameterNotEmpty("location", _projectLocation);
        validateDirectoryExists(_projectLocation, _projectName);
    }

}
