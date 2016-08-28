package pro.documentum.demo.isis.dom;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.isis.applib.AppManifest;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import com.google.common.collect.Lists;

/**
 * Provided for <tt>isis-maven-plugin</tt>.
 */
public class DomainAppDomManifest implements AppManifest {

    public DomainAppDomManifest() {
        super();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Class<?>> getModules() {
        return Lists.<Class<?>> newArrayList(DomainAppDomainModule.class);
    }

    @Override
    public List<Class<?>> getAdditionalServices() {
        return Collections.emptyList();
    }

    @Override
    public String getAuthenticationMechanism() {
        return null;
    }

    @Override
    public String getAuthorizationMechanism() {
        return null;
    }

    @Override
    public List<Class<? extends FixtureScript>> getFixtures() {
        return null;
    }

    /**
     * No overrides.
     */
    @Override
    public Map<String, String> getConfigurationProperties() {
        return null;
    }

}
