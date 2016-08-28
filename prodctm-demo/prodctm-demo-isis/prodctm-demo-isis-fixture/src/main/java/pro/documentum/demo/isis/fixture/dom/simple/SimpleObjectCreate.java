
package pro.documentum.demo.isis.fixture.dom.simple;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import pro.documentum.demo.isis.dom.simple.SimpleObject;
import pro.documentum.demo.isis.dom.simple.SimpleObjects;

public class SimpleObjectCreate extends FixtureScript {

    //region > name (input)
    private String name;
    /**
     * Name of the object (required)
     */
    public String getName() {
        return name;
    }

    public SimpleObjectCreate setName(final String name) {
        this.name = name;
        return this;
    }
    //endregion


    //region > simpleObject (output)
    private SimpleObject simpleObject;

    /**
     * The created simple object (output).
     * @return
     */
    public SimpleObject getSimpleObject() {
        return simpleObject;
    }
    //endregion

    @Override
    protected void execute(final ExecutionContext ec) {

        String name = checkParam("name", ec, String.class);

        this.simpleObject = wrap(simpleObjects).create(name);

        // also make available to UI
        ec.addResult(this, simpleObject);
    }

    @javax.inject.Inject
    private SimpleObjects simpleObjects;

}
