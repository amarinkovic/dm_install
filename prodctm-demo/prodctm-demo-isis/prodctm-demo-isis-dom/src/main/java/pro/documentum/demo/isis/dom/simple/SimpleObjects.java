package pro.documentum.demo.isis.dom.simple;

import java.util.List;

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(nature = NatureOfService.VIEW, repositoryFor = SimpleObject.class)
@DomainServiceLayout(menuOrder = "10")
public class SimpleObjects {

    // region > title
    public TranslatableString title() {
        return TranslatableString.tr("Simple Objects");
    }

    // endregion

    // region > listAll (action)
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    @MemberOrder(sequence = "1")
    public List<SimpleObject> listAll() {
        return repositoryService.allInstances(SimpleObject.class);
    }

    // endregion

    // region > findByName (action)
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(bookmarking = BookmarkPolicy.AS_ROOT)
    @MemberOrder(sequence = "2")
    public List<SimpleObject> findByName(
            @ParameterLayout(named = "Name") final String name) {
        return repositoryService.allMatches(new QueryDefault<>(
                SimpleObject.class, "findByName", "name", name));
    }

    // endregion

    // region > create (action)
    public static class CreateDomainEvent extends
            ActionDomainEvent<SimpleObjects> {
        public CreateDomainEvent(final SimpleObjects source,
                final Identifier identifier, final Object... arguments) {
            super(source, identifier, arguments);
        }
    }

    @Action(domainEvent = CreateDomainEvent.class)
    @MemberOrder(sequence = "3")
    public SimpleObject create(
            final @ParameterLayout(named = "Name") String name) {
        final SimpleObject obj = repositoryService
                .instantiate(SimpleObject.class);
        obj.setObjectName(name);
        repositoryService.persist(obj);
        return obj;
    }

    // endregion

    // region > injected services

    @javax.inject.Inject
    RepositoryService repositoryService;

    // endregion
}
