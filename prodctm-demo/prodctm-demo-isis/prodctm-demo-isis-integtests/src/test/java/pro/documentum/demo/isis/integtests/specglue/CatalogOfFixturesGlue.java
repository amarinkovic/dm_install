package pro.documentum.demo.isis.integtests.specglue;

import org.apache.isis.core.specsupport.specs.CukeGlueAbstract;

import cucumber.api.java.Before;
import pro.documentum.demo.isis.fixture.scenarios.RecreateSimpleObjects;

public class CatalogOfFixturesGlue extends CukeGlueAbstract {

    @Before(value={"@integration", "@SimpleObjectsFixture"}, order=20000)
    public void integrationFixtures() throws Throwable {
        scenarioExecution().install(new RecreateSimpleObjects());
    }

}
