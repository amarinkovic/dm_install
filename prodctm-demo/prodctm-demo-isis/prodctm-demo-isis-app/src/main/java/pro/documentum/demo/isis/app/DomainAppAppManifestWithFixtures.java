package pro.documentum.demo.isis.app;

import java.util.List;
import java.util.Map;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import pro.documentum.demo.isis.fixture.scenarios.RecreateSimpleObjects;

public class DomainAppAppManifestWithFixtures extends DomainAppAppManifest {

    public DomainAppAppManifestWithFixtures() {
        super();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Class<? extends FixtureScript>> getFixtures() {
        return Lists
                .<Class<? extends FixtureScript>> newArrayList(RecreateSimpleObjects.class);
    }

    @Override
    public Map<String, String> getConfigurationProperties() {
        Map<String, String> props = super.getConfigurationProperties();
        props = Maps.newHashMap(props);
        props.put("isis.persistor.datanucleus.install-fixtures", "true");
        return props;
    }

}
