
package pro.documentum.demo.isis.fixture.dom.simple;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

public class SimpleObjectsTearDown extends FixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {
        isisJdoSupport.executeUpdate("delete from \"simple\".\"SimpleObject\"");
    }


    @javax.inject.Inject
    private IsisJdoSupport isisJdoSupport;

}
