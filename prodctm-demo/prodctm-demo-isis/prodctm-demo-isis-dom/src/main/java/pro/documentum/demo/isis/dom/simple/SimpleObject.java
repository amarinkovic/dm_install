package pro.documentum.demo.isis.dom.simple;

import javax.jdo.annotations.PersistenceCapable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.services.eventbus.PropertyDomainEvent;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.util.ObjectContracts;

import pro.documentum.model.jdo.sysobject.DmDocument;

@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(name = "find", language = "JDOQL", value = "SELECT "
            + "FROM pro.documentum.demo.isis.dom.simple.SimpleObject "),
    @javax.jdo.annotations.Query(name = "findByName", language = "JDOQL", value = "SELECT "
            + "FROM pro.documentum.demo.isis.dom.simple.SimpleObject "
            + "WHERE objectName.indexOf(:name) >= 0 ") })
@DomainObject
@PersistenceCapable(table = "dm_document")
public class SimpleObject extends DmDocument implements
        Comparable<SimpleObject> {

    public static final int NAME_LENGTH = 40;

    public TranslatableString title() {
        return TranslatableString.tr("Object: {name}", "name", getObjectName());
    }

    public static class NameDomainEvent extends
            PropertyDomainEvent<SimpleObject, String> {
    }

    @Override
    @Property(domainEvent = NameDomainEvent.class)
    public String getObjectName() {
        return super.getObjectName();
    }

    public TranslatableString validateObjectName(final String name) {
        return name != null && name.contains("!") ? TranslatableString
                .tr("Exclamation mark is not allowed") : null;
    }

    public static class DeleteDomainEvent extends
            ActionDomainEvent<SimpleObject> {
    }

    @Action(domainEvent = DeleteDomainEvent.class, semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public void delete() {
        repositoryService.remove(this);
    }

    @Override
    public int compareTo(final SimpleObject other) {
        return ObjectContracts.compare(this, other, "name");
    }

    @javax.inject.Inject
    RepositoryService repositoryService;

}
