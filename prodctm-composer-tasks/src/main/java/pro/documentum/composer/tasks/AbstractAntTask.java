package pro.documentum.composer.tasks;

import java.io.File;
import java.net.URI;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;

import com.emc.ide.logger.IDmLogger;
import com.emc.ide.project.DmCoreProjectUtils;
import com.emc.ide.project.IDmProject;
import com.emc.ide.project.IDmProjectFactory;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class AbstractAntTask extends Task {

    public AbstractAntTask() {
        super();
    }

    protected IProject validateProjectExists(final String projectName)
        throws BuildException {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = root.getProject(projectName);
        if (!project.exists()) {
            throw new BuildException("Project '" + projectName
                    + "' does not exist in the workspace");
        }
        return project;
    }

    protected IDmProject validateIsDocumentumProject(final String projectName) {
        IProject project = validateProjectExists(projectName);
        IDmProjectFactory theDmProjectFactory = IDmProjectFactory.INSTANCE;
        try {
            IDmProject dmProject = theDmProjectFactory.loadProject(project,
                    null);
            if (dmProject == null) {
                throw new BuildException("Project '" + projectName
                        + "' is not a Documentum project");
            }
            return dmProject;
        } catch (CoreException ex) {
            throw new BuildException("Error loading Documentum project '"
                    + projectName + "'", ex);
        }
    }

    protected void validateParameterNotEmpty(final String parameterName,
            final String value) throws BuildException {
        if (value == null || value.length() == 0) {
            throw new BuildException("Missing parameter: " + parameterName);
        }
    }

    protected void validateDirectoryExists(final String path)
        throws BuildException {
        File location = new File(path);
        if (!location.exists()) {
            throw new BuildException("Path " + location.getPath()
                    + " does not exists");
        }
        if (!location.isDirectory()) {
            throw new BuildException("Path " + location.getPath()
                    + "is not a directory");
        }
    }

    protected void validateDirectoryExists(final String path,
            final String... children) throws BuildException {
        validateDirectoryExists(path);
        File location = new File(path);
        for (String child : children) {
            location = new File(location, child);
            validateDirectoryExists(location.getPath());
        }
    }

    protected void createCoreProject(final IWorkspaceRoot workspaceRoot,
            final IProgressMonitor progressMonitor) throws CoreException {
        boolean needCreateCoreProject = true;
        for (IProject projectInWorkspace : workspaceRoot.getProjects()) {
            loadExistingProject(projectInWorkspace, progressMonitor);
            if ("DocumentumCoreProject".equals(projectInWorkspace.getName())) {
                needCreateCoreProject = false;
            }
        }

        if (needCreateCoreProject) {
            createCoreProject(progressMonitor);
        }
    }

    protected void createCoreProject(final IProgressMonitor progressMonitor)
        throws CoreException {
        info("Creating core project");
        progressMonitor.beginTask("creating core project", 0);
        if (progressMonitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        IDmProjectFactory.INSTANCE.createRequiredCoreProjects(progressMonitor);
    }

    protected IProject importProject(final String projectName,
            final File projectFile, final IWorkspace workspace,
            final IProgressMonitor progressMonitor) throws CoreException {
        trace("Importing project " + projectName + " from "
                + projectFile.getAbsolutePath());
        IProject project = workspace.getRoot().getProject(projectName);
        IProjectDescription projectDescription = workspace
                .loadProjectDescription(new Path(projectFile.getAbsolutePath()));

        IProject[] referenced = projectDescription.getReferencedProjects();
        for (IProject reference : referenced) {
            if (reference.exists()) {
                continue;
            }
            throw new BuildException("Reference project " + reference.getName()
                    + " does not exists");
        }

        project.create(projectDescription, new SubProgressMonitor(
                progressMonitor, 30));
        project.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(
                progressMonitor, 70));
        return project;
    }

    protected void loadExistingProject(final IProject project,
            final IProgressMonitor progressMonitor) throws CoreException {
        if (!project.isOpen()) {
            IDmLogger.Logger.info("Project " + project.getName()
                    + " is not open");
            return;
        }
        if (!DmCoreProjectUtils.isDmProject(project)) {
            IDmLogger.Logger.info("Project " + project.getName()
                    + " is not a Documentum project");
            return;
        }
        trace("Loading existing project " + project.getName());
        progressMonitor.beginTask("Loading project: " + project.getName(), 0);
        IDmProjectFactory.INSTANCE.loadProject(project, progressMonitor);
    }

    protected boolean isProjectInWorkspaceDirectory(
            final IWorkspaceRoot workspaceRoot, final IProject project)
        throws CoreException {
        IPath workspaceRootPath = workspaceRoot.getLocation();
        URI projectLocation = project.getLocationURI();
        IPath projectPath = new Path(projectLocation.getPath());
        return workspaceRootPath.isPrefixOf(projectPath);
    }

    protected void copyDirectory(final File srcDir, final File dstDir) {
        Copy copy = new Copy();
        Project project = new Project();
        copy.setTodir(dstDir);
        FileSet fileSet = new FileSet();
        fileSet.setIncludes("**/**");
        fileSet.setDir(srcDir);
        copy.add(fileSet);
        copy.setProject(project);
        copy.execute();
    }

    protected void copyFile(final File srcFile, final File dstDir) {
        Copy copy = new Copy();
        Project project = new Project();
        copy.setTodir(dstDir);
        copy.setFile(srcFile);
        copy.setProject(project);
        copy.execute();
    }

    protected void info(final String message) {
        IDmLogger.Logger.info(message);
    }

    protected void trace(final String message) {
        IDmLogger.Logger.trace(message);
    }

}
