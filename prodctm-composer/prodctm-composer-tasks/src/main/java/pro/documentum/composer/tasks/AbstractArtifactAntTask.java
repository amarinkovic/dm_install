package pro.documentum.composer.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EMap;

import com.emc.ide.artifact.dardef.model.dardef.ArtifactOptionsSet;
import com.emc.ide.artifact.dardef.model.dardef.DardefFactory;
import com.emc.ide.artifact.dardef.model.dardef.UpgradeOption;
import com.emc.ide.artifact.dardef.model.dardef.UpgradeOptionValues;
import com.emc.ide.artifactmanager.IDmArtifactManager;
import com.emc.ide.artifactmanager.artifact.DmArtifactFilter;
import com.emc.ide.artifactmanager.artifact.IDmArtifact;
import com.emc.ide.artifactmanager.filters.CurrentProjectFilter;
import com.emc.ide.artifactmanager.model.artifact.IArtifactDataModel;
import com.emc.ide.artifactmanager.service.IDmLocatorService;
import com.emc.ide.core.model.core.IDarDef;
import com.emc.ide.importer.IImportContentHandlerDelegate;
import com.emc.ide.importer.exception.ImportContentException;
import com.emc.ide.project.IDmProject;
import com.emc.ide.util.DmProjectUtils;

import pro.documentum.composer.tasks.filters.FilterArtifactByCategory;
import pro.documentum.composer.tasks.filters.FilterArtifactByName;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public abstract class AbstractArtifactAntTask extends AbstractAntTask {

    private Map<String, IImportContentHandlerDelegate> _contentHandlers;

    public AbstractArtifactAntTask() {
        super();
        _contentHandlers = initExtensionBasedContentHandlerDelegates();
    }

    protected void importContent(final IDmProject project,
            final Artifacts artifacts) throws CoreException {
        if (artifacts == null || artifacts.getArtifacts().isEmpty()) {
            trace("Nothing to do");
            return;
        }

        IDmLocatorService locatorService = IDmArtifactManager.INSTANCE
                .getLocatorService(project.getEclipseProject());
        for (Artifact artifact : artifacts.getArtifacts()) {
            importContentFile(project, artifact, locatorService);
        }
    }

    private void importContentFile(final IDmProject project,
            final Artifact option, final IDmLocatorService locatorService)
        throws BuildException, CoreException {
        trace("Locating artifact with name " + option.getName()
                + " and category " + option.getCategory());
        IProject eclipseProject = project.getEclipseProject();
        DmArtifactFilter projectFilter = new CurrentProjectFilter(
                eclipseProject);
        DmArtifactFilter nameFilter = new FilterArtifactByName(option);
        DmArtifactFilter categoryFilter = new FilterArtifactByCategory(option);
        projectFilter.appendFilter(nameFilter);
        nameFilter.appendFilter(categoryFilter);
        IDmArtifact[] artifacts = locatorService.getAllArtifacts(projectFilter);

        if (artifacts == null || artifacts.length == 0) {
            trace("Project " + eclipseProject.getName()
                    + " does not contain matched artifacts");
            return;
        }
        try {
            for (IDmArtifact artifact : artifacts) {
                String strCategoryId = artifact.getCategory().getCategoryId();
                IImportContentHandlerDelegate delegate = _contentHandlers
                        .get(strCategoryId);
                if (delegate == null) {
                    throw new BuildException(
                            "Content handler not found for category "
                                    + strCategoryId);
                }
                InputStream stream = new FileInputStream(
                        resolvePath(option.getContentPath()));
                delegate.doImport(artifact, stream, null);
                artifact.save();
            }
        } catch (FileNotFoundException ex) {
            throw new BuildException(ex);
        } catch (ImportContentException ex) {
            throw new BuildException(ex);
        }
    }

    protected void setUpgradeOptions(final IDmProject project,
            final Artifacts artifacts) throws CoreException {
        if (artifacts == null || artifacts.getArtifacts().isEmpty()) {
            trace("Nothing to do");
            return;
        }

        IProject eclipseProject = project.getEclipseProject();

        IDmLocatorService locatorService = IDmArtifactManager.INSTANCE
                .getLocatorService(eclipseProject);
        IDmArtifact darArtifact = null;
        try {
            IFile darFile = DmProjectUtils.findSourceDarDef(project
                    .getEclipseProject());
            darArtifact = locatorService.getCachedArtifactByFile(darFile);
        } catch (FileNotFoundException ex) {
            throw new BuildException(
                    "Unable to locate dar definition in project "
                            + eclipseProject.getName(), ex);
        }
        IDarDef darDef = (IDarDef) darArtifact.getDataModel();
        EMap<?, ?> installOptions = (EMap<?, ?>) darDef.getInstallOptionsMap();
        for (Artifact artifact : artifacts.getArtifacts()) {
            setUpgradeOption(project, artifact, locatorService, installOptions);
        }
        darArtifact.save();
    }

    private void setUpgradeOption(final IDmProject project,
            final Artifact option, final IDmLocatorService locatorService,
            final EMap installOptions) throws CoreException {
        trace("Locating artifact with name " + option.getName()
                + " and category " + option.getCategory());
        IProject eclipseProject = project.getEclipseProject();
        DmArtifactFilter projectFilter = new CurrentProjectFilter(
                eclipseProject);
        DmArtifactFilter nameFilter = new FilterArtifactByName(option);
        DmArtifactFilter categoryFilter = new FilterArtifactByCategory(option);
        projectFilter.appendFilter(nameFilter);
        nameFilter.appendFilter(categoryFilter);
        IDmArtifact[] artifacts = locatorService.getAllArtifacts(projectFilter);

        if (artifacts == null || artifacts.length == 0) {
            trace("Project " + eclipseProject.getName()
                    + " does not contain matched artifacts");
            return;
        }

        for (IDmArtifact artifact : artifacts) {
            IArtifactDataModel dataModel = artifact.getDataModel();
            ArtifactOptionsSet installOption = (ArtifactOptionsSet) installOptions
                    .get(dataModel);
            if (installOption == null) {
                trace("No installation options for artifact "
                        + artifact.getName() + " in project "
                        + eclipseProject.getName() + ", creating new one");
                installOption = DardefFactory.eINSTANCE
                        .createArtifactOptionsSet();
                installOptions.put(dataModel, installOption);
            }
            UpgradeOption processUpgradeOption = DardefFactory.eINSTANCE
                    .createUpgradeOption();
            UpgradeOptionValues upgradeOptionValues = UpgradeOptionValues
                    .getByName(option.getUpgradeOption());
            if (upgradeOptionValues == null) {
                throw new BuildException("Invalid upgrade option "
                        + option.getUpgradeOption() + " for artifact "
                        + artifact.getName() + " ("
                        + artifact.getCategory().getCategoryId() + ")");
            }
            info("Setting upgrade option to " + upgradeOptionValues.getName()
                    + " for artifact " + artifact.getName() + " ("
                    + artifact.getCategory().getCategoryId() + ")");
            processUpgradeOption.setOptionValue(upgradeOptionValues);
            installOption.setUpgradeOption(processUpgradeOption);
        }
    }

    protected List<String> readLines(final String file) throws BuildException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            List<String> list = new ArrayList<String>();
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    list.add(line);
                }
                line = reader.readLine();
            }
            return list;
        } catch (IOException ex) {
            throw new BuildException(ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    // ingore
                }
            }
        }
    }

    protected File resolvePath(final String path) throws FileNotFoundException {
        File file = new File(path);
        if (file.exists()) {
            return file;
        }
        file = AccessController.doPrivileged(new PrivilegedAction<File>() {
            @Override
            public File run() {
                String value = System.getenv(path);
                if (value != null) {
                    return new File(value);
                }
                value = System.getProperty(path);
                if (value != null) {
                    return new File(value);
                }
                return null;
            }
        });
        if (file != null && file.exists()) {
            return file;
        }
        throw new FileNotFoundException(path);
    }

    private Map<String, IImportContentHandlerDelegate> initExtensionBasedContentHandlerDelegates() {
        Map<String, IImportContentHandlerDelegate> handlers = new HashMap<String, IImportContentHandlerDelegate>();
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry
                .getExtensionPoint("com.emc.ide.importer.contentHandlerDelegates");
        if (point == null) {
            return handlers;
        }
        IExtension[] extensions = point.getExtensions();
        for (IExtension extension : extensions) {
            processExtension(extension, handlers);
        }
        return handlers;
    }

    private void processExtension(final IExtension extension,
            final Map<String, IImportContentHandlerDelegate> handlers) {
        IConfigurationElement[] configElements = extension
                .getConfigurationElements();
        for (IConfigurationElement configElement : configElements) {
            processConfigElement(configElement, handlers);
        }
    }

    private void processConfigElement(
            final IConfigurationElement configElement,
            final Map<String, IImportContentHandlerDelegate> handlers) {
        try {
            String strEClass = configElement.getAttribute("artifactCategory");
            IImportContentHandlerDelegate objectClass = (IImportContentHandlerDelegate) configElement
                    .createExecutableExtension("delegate");
            handlers.put(strEClass, objectClass);
        } catch (CoreException ce) {
            error("Unable to initialize content handler delegate", ce);
        }
    }

}
