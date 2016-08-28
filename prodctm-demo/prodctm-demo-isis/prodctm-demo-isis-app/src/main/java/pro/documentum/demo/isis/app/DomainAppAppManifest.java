package pro.documentum.demo.isis.app;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.isis.applib.AppManifest;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.core.runtime.system.SystemConstants;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import pro.documentum.demo.isis.dom.DomainAppDomainModule;
import pro.documentum.demo.isis.fixture.DomainAppFixtureModule;
import pro.documentum.ext.isis.AuthenticationManagerInstaller;
import pro.documentum.ext.isis.AuthorizationManagerInstaller;
import pro.documentum.ext.isis.PersistenceMechanismInstaller;

public class DomainAppAppManifest implements AppManifest {

    public DomainAppAppManifest() {
        super();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Class<?>> getModules() {
        return Lists.<Class<?>> newArrayList(DomainAppDomainModule.class,
                DomainAppFixtureModule.class, DomainAppAppModule.class);
    }

    @Override
    public List<Class<?>> getAdditionalServices() {
        return Collections.emptyList();
    }

    @Override
    public String getAuthenticationMechanism() {
        return AuthenticationManagerInstaller.class.getName();
    }

    @Override
    public String getAuthorizationMechanism() {
        return AuthorizationManagerInstaller.class.getName();
    }

    @Override
    public List<Class<? extends FixtureScript>> getFixtures() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, String> getConfigurationProperties() {
        HashMap<String, String> props = Maps.newHashMap();
        props.put(SystemConstants.OBJECT_PERSISTOR_KEY,
                PersistenceMechanismInstaller.class.getName());
        return props;
    }

}
