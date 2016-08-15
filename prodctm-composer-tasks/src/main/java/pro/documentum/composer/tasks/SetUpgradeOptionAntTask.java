package pro.documentum.composer.tasks;

import java.io.FileNotFoundException;
import java.text.MessageFormat;

import org.apache.tools.ant.BuildException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EMap;

import com.emc.ide.artifact.dardef.model.dardef.ArtifactOptionsSet;
import com.emc.ide.artifact.dardef.model.dardef.DardefFactory;
import com.emc.ide.artifact.dardef.model.dardef.UpgradeOption;
import com.emc.ide.artifact.dardef.model.dardef.UpgradeOptionValues;
import com.emc.ide.artifactmanager.IDmArtifactManager;
import com.emc.ide.artifactmanager.artifact.DmArtifactFilter;
import com.emc.ide.artifactmanager.artifact.IDmArtifact;
import com.emc.ide.artifactmanager.model.artifact.IArtifactDataModel;
import com.emc.ide.artifactmanager.service.IDmLocatorService;
import com.emc.ide.artifactmanager.uriconverter.IUrnFinderFactory;
import com.emc.ide.core.model.core.IDarDef;
import com.emc.ide.logger.dbc.DBC;
import com.emc.ide.project.IDmProject;
import com.emc.ide.util.DmProjectUtils;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class SetUpgradeOptionAntTask extends AbstractAntTask {

    private static final String TASK_NAME = "pro.setUpgradeOption";

    private String _projectName;

    private IDmProject _dmProject;

    private UpgradeOptions _upgradeOptions;

    public SetUpgradeOptionAntTask() {
        super();
    }

    public void setProject(final String name) {
        info("Parameter 'project' is '" + name + "'");
        _projectName = name;
    }

    public void addUpgradeOptions(final UpgradeOptions options) {
        _upgradeOptions = options;
    }

    public void execute() throws BuildException {
        setTaskName(TASK_NAME);
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
    }

    private void setUpgradeOption() throws CoreException {
        if (_upgradeOptions == null || _upgradeOptions.getArtifacts().isEmpty()) {
            info("Nothing to do");
            return;
        }

        IDmLocatorService locatorService = IDmArtifactManager.INSTANCE
                .getLocatorService(_dmProject.getEclipseProject());
        IDmArtifact darArtifact = null;
        try {
            IFile darFile = DmProjectUtils.findSourceDarDef(_dmProject
                    .getEclipseProject());
            darArtifact = locatorService.getCachedArtifactByFile(darFile);
        } catch (FileNotFoundException ex) {
            throw new BuildException(
                    "Unable to locate dar definition in project "
                            + _projectName, ex);
        }
        IDarDef darDef = (IDarDef) darArtifact.getDataModel();
        EMap<?, ?> installOptions = (EMap<?, ?>) darDef.getInstallOptionsMap();
        for (Artifact artifact : _upgradeOptions.getArtifacts()) {
            setUpgradeOption(artifact, locatorService, installOptions);
        }
        darArtifact.save();
    }

    private void setUpgradeOption(final Artifact option,
            final IDmLocatorService locatorService, final EMap installOptions)
        throws CoreException {
        info("Locating artifact with name " + option.getName()
                + " and category " + option.getCategory());
        IDmArtifact[] artifacts = locatorService.getArtifactsByCategory(
                option.getCategory(), new LocateArtifactByName(option));
        if (artifacts == null || artifacts.length == 0) {
            info("Unable locate artifact " + option.getName() + " in project "
                    + _projectName);
            return;
        }

        for (IDmArtifact artifact : artifacts) {
            IArtifactDataModel dataModel = artifact.getDataModel();
            ArtifactOptionsSet installOption = (ArtifactOptionsSet) installOptions
                    .get(dataModel);
            if (installOption == null) {
                info("No installation options for artifact "
                        + artifact.getName() + " in project " + _projectName
                        + ", creating new one");
                installOption = DardefFactory.eINSTANCE
                        .createArtifactOptionsSet();
                installOptions.put(dataModel, installOption);
            }
            UpgradeOption processUpgradeOption = DardefFactory.eINSTANCE
                    .createUpgradeOption();
            UpgradeOptionValues upgradeOptionValues = UpgradeOptionValues
                    .getByName(option.getValue());
            info("Setting upgrade option to " + upgradeOptionValues.getName()
                    + " for artifact " + artifact.getName() + " in project "
                    + _projectName);
            processUpgradeOption.setOptionValue(upgradeOptionValues);
            installOption.setUpgradeOption(processUpgradeOption);
        }
    }

    private void validateArgs() throws BuildException {
        validateParameterNotEmpty("project", _projectName);
        _dmProject = validateIsDocumentumProject(_projectName);

        if (_upgradeOptions == null || _upgradeOptions.getArtifacts().isEmpty()) {
            info("No upgrade options specified");
        }

        for (Artifact artifact : _upgradeOptions.getArtifacts()) {
            validateParameterNotEmpty("name", artifact.getName());
            validateParameterNotEmpty("category", artifact.getCategory());
            validateParameterNotEmpty("option", artifact.getValue());

            UpgradeOptionValues values = UpgradeOptionValues.getByName(artifact
                    .getValue());
            if (values != null) {
                continue;
            }
            throw new BuildException(MessageFormat.format(
                    "Invalid upgrade option {0}", artifact.getValue()));
        }
    }

    private class LocateArtifactByName extends DmArtifactFilter {

        private final Artifact _option;

        LocateArtifactByName(final Artifact option) {
            _option = option;
        }

        @Override
        protected boolean doAccept(final IDmArtifact artifact) {
            DBC.preCondition(artifact != null, "artifact must not be null");
            if (!artifact.isModifiable()) {
                return false;
            }
            if (artifact.getName().startsWith("dm_")
                    || artifact.getName().startsWith("dmc_")) {
                return false;
            }
            if (_option.getName().equals(artifact.getName())) {
                info("Found artifact " + artifact.getName()
                        + " via name in project " + _projectName);
                return true;
            }
            if ("*".equals(_option.getName())) {
                info("Found artifact " + artifact.getName()
                        + " via wildcard in project " + _projectName);
                return true;
            }
            return false;
        }
    }

}
