package pro.documentum.ext.isis;

import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.commons.config.IsisConfigurationDefault;
import org.apache.isis.core.metamodel.services.ServicesInjectorSpi;
import org.apache.isis.core.metamodel.spec.SpecificationLoaderSpi;
import org.apache.isis.core.runtime.system.DeploymentType;
import org.apache.isis.core.runtime.system.persistence.PersistenceSession;

import pro.documentum.util.java.decorators.Decorators;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class PersistenceSessionFactory
        extends
        org.apache.isis.core.runtime.system.persistence.PersistenceSessionFactory {

    public PersistenceSessionFactory(final DeploymentType deploymentType,
            final IsisConfigurationDefault isisConfiguration) {
        super(deploymentType, isisConfiguration);
    }

    @Override
    public PersistenceSession createPersistenceSession(
            final ServicesInjectorSpi servicesInjector,
            final SpecificationLoaderSpi specificationLoader,
            final AuthenticationSession authenticationSession) {
        return Decorators.wrap(new PersistenceSessionDecorator(super
                .createPersistenceSession(servicesInjector,
                        specificationLoader, authenticationSession),
                authenticationSession));
    }

}
