package pro.documentum.ext.isis;

import org.apache.isis.core.commons.config.IsisConfigurationDefault;
import org.apache.isis.core.metamodel.services.ServicesInjectorSpi;
import org.apache.isis.core.runtime.system.DeploymentType;
import org.apache.isis.objectstore.jdo.datanucleus.DataNucleusPersistenceMechanismInstaller;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PersistenceMechanismInstaller extends
        DataNucleusPersistenceMechanismInstaller {

    public static final String NAME = "documentum";

    public PersistenceMechanismInstaller() {
        super();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public org.apache.isis.core.runtime.system.persistence.PersistenceSessionFactory createPersistenceSessionFactory(
            final DeploymentType deploymentType,
            final IsisConfigurationDefault configuration,
            final ServicesInjectorSpi servicesInjector) {
        return new PersistenceSessionFactory(deploymentType, configuration);
    }

}
