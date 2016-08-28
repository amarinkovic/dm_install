package pro.documentum.demo.isis.app.services.homepage;

import java.util.List;

import org.apache.isis.applib.annotation.ViewModel;

import pro.documentum.demo.isis.dom.simple.SimpleObject;
import pro.documentum.demo.isis.dom.simple.SimpleObjects;

@ViewModel
public class HomePageViewModel {

    //region > title
    public String title() {
        return "Objects";
    }
    //endregion

    //region > object (collection)
    @org.apache.isis.applib.annotation.HomePage
    public List<SimpleObject> getObjects() {
        return simpleObjects.listAll();
    }
    //endregion

    //region > injected services

    @javax.inject.Inject
    SimpleObjects simpleObjects;

    //endregion
}
