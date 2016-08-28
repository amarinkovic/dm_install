package pro.documentum.demo.isis.integtests.specglue.modules.simple;

import java.util.List;
import java.util.UUID;

import org.apache.isis.core.specsupport.specs.CukeGlueAbstract;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import pro.documentum.demo.isis.dom.simple.SimpleObject;
import pro.documentum.demo.isis.dom.simple.SimpleObjects;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SimpleObjectGlue extends CukeGlueAbstract {

    @Given("^there are.* (\\d+) simple objects$")
    public void there_are_N_simple_objects(int n) throws Throwable {
        try {
            final List<SimpleObject> findAll = service(SimpleObjects.class).listAll();
            assertThat(findAll.size(), is(n));
            putVar("list", "all", findAll);
            
        } finally {
            assertMocksSatisfied();
        }
    }
    
    @When("^I create a new simple object$")
    public void I_create_a_new_simple_object() throws Throwable {
        service(SimpleObjects.class).create(UUID.randomUUID().toString());
    }
    
}
