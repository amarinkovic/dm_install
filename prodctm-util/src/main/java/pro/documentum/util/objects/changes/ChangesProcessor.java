package pro.documentum.util.objects.changes;

import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

import pro.documentum.util.IDfSessionInvoker;
import pro.documentum.util.convert.Converter;
import pro.documentum.util.logger.Logger;
import pro.documentum.util.objects.changes.attributes.IAttributeHandler;
import pro.documentum.util.objects.changes.attributes.PersistentHandler;
import pro.documentum.util.objects.changes.attributes.content.ContentReadOnlyHandler;
import pro.documentum.util.objects.changes.attributes.content.ParentIdHandler;
import pro.documentum.util.objects.changes.attributes.persistent.AspectNameHandler;
import pro.documentum.util.objects.changes.attributes.persistent.ReadOnlyHandler;
import pro.documentum.util.objects.changes.attributes.sysobject.FolderHandler;
import pro.documentum.util.objects.changes.attributes.sysobject.ObjectNameHandler;
import pro.documentum.util.objects.changes.attributes.sysobject.PolicyHandler;
import pro.documentum.util.objects.changes.attributes.sysobject.SysObjectReadOnlyHandler;
import pro.documentum.util.objects.changes.attributes.sysobject.TitleHandler;
import pro.documentum.util.objects.changes.attributes.sysobject.VersionHandler;
import pro.documentum.util.objects.changes.attributes.user.UserPermitHandler;
import pro.documentum.util.objects.changes.attributes.user.UserReadOnlyHandler;
import pro.documentum.util.objects.changes.attributes.workitem.OutputPortHandler;
import pro.documentum.util.objects.changes.attributes.workitem.PerformerHandler;
import pro.documentum.util.objects.changes.attributes.workitem.RuntimeStateHandler;
import pro.documentum.util.sessions.Sessions;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class ChangesProcessor {

    private static final Map<Class<? extends IAttributeHandler>, IAttributeHandler> REGISTRY;

    static {
        REGISTRY = new HashMap<Class<? extends IAttributeHandler>, IAttributeHandler>();
    }

    static {
        // aspects
        addAttributeHandler(AspectNameHandler.class);
        // workitems
        addAttributeHandler(OutputPortHandler.class);
        addAttributeHandler(PerformerHandler.class);
        addAttributeHandler(RuntimeStateHandler.class);
        // sysobjects
        addAttributeHandler(ObjectNameHandler.class);
        addAttributeHandler(TitleHandler.class);
        addAttributeHandler(FolderHandler.class);
        addAttributeHandler(VersionHandler.class);
        addAttributeHandler(PolicyHandler.class);
        // users
        addAttributeHandler(UserPermitHandler.class);
        addAttributeHandler(PersistentHandler.class);
        // content
        addAttributeHandler(ParentIdHandler.class);
        // read-only
        addAttributeHandler(ReadOnlyHandler.class);
        addAttributeHandler(UserReadOnlyHandler.class);
        addAttributeHandler(SysObjectReadOnlyHandler.class);
        addAttributeHandler(ContentReadOnlyHandler.class);
    }

    private ChangesProcessor() {
        super();
    }

    public static void process(final IDfPersistentObject object,
            final Map<String, ?> values) throws DfException {
        Map<String, Object> data = prepare(object, values);
        Queue<IAttributeHandler> handlers = getHandlers(object, data);
        if (handlers == null) {
            Logger.debug("Nothing to do for object ", object.getObjectId());
            return;
        }
        Sessions.inTransaction(object.getObjectSession(),
                new TransactionalChanges(object, data, handlers));
    }

    private static Queue<IAttributeHandler> getHandlers(
            final IDfPersistentObject object, final Map<String, Object> data)
        throws DfException {
        Queue<IAttributeHandler> handlers = new ArrayDeque<IAttributeHandler>();
        for (IAttributeHandler handler : REGISTRY.values()) {
            if (!handler.accept(object, data.keySet())) {
                continue;
            }
            handlers = buildDependencies(handler, handlers);
        }
        // this one is default handler and always returns false for accept
        handlers.offer(getAttributeHandler(PersistentHandler.class));
        return handlers;
    }

    private static Map<String, Object> prepare(
            final IDfPersistentObject object, final Map<String, ?> values)
        throws DfException {
        try {
            Converter converter = Converter.getInstance();
            Map<String, Object> result = new HashMap<String, Object>();
            for (String attrName : values.keySet()) {
                if (!object.hasAttr(attrName)) {
                    continue;
                }
                int dataType = object.getAttrDataType(attrName);
                Object value = converter
                        .convert(values.get(attrName), dataType);
                result.put(attrName, value);
            }
            return result;
        } catch (ParseException ex) {
            throw new DfException(ex);
        }
    }

    private static Queue<IAttributeHandler> buildDependencies(
            final IAttributeHandler<?> handler,
            final Queue<IAttributeHandler> handlers) {
        Queue<IAttributeHandler> dependencies = handlers;
        if (dependencies == null) {
            dependencies = new ArrayDeque<IAttributeHandler>();
        }
        if (dependencies.contains(handler)) {
            return dependencies;
        }
        for (Class<? extends IAttributeHandler> cls : handler.getDependencies()) {
            if (cls == handler.getClass()) {
                continue;
            }
            dependencies = buildDependencies(getAttributeHandler(cls),
                    dependencies);
        }
        if (!dependencies.contains(handler)) {
            dependencies.offer(handler);
        }
        return dependencies;
    }

    private static IAttributeHandler getAttributeHandler(
            final Class<? extends IAttributeHandler> cls) {
        return REGISTRY.get(cls);
    }

    private static void addAttributeHandler(
            final Class<? extends IAttributeHandler> cls) {
        if (REGISTRY.containsKey(cls)) {
            return;
        }
        // noinspection TryWithIdenticalCatches
        try {
            IAttributeHandler<?> handler = cls.newInstance();
            REGISTRY.put(cls, handler);
            for (Class<? extends IAttributeHandler> req : handler
                    .getDependencies()) {
                addAttributeHandler(req);
            }
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    static class TransactionalChanges implements IDfSessionInvoker<Void> {

        private final IDfPersistentObject _object;

        private final Map<String, ?> _data;

        private final Queue<IAttributeHandler> _handlers;

        TransactionalChanges(final IDfPersistentObject object,
                final Map<String, ?> data,
                final Queue<IAttributeHandler> handlers) {
            _object = object;
            _data = data;
            _handlers = handlers;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Void invoke(final IDfSession session) throws DfException {
            while (!_handlers.isEmpty()) {
                IAttributeHandler handler = _handlers.remove();
                if (handler.apply(_object, _data)) {
                    _handlers.offer(handler);
                }
            }
            IAttributeHandler handler = getAttributeHandler(PersistentHandler.class);
            handler.apply(_object, _data);
            return null;
        }

    }

}
