package pro.documentum.util.objects.changes;

import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;

import pro.documentum.util.convert.Converter;
import pro.documentum.util.objects.changes.attributes.IAttributeHandler;
import pro.documentum.util.objects.changes.attributes.PersistentHandler;
import pro.documentum.util.objects.changes.attributes.persistent.AspectNameHandler;
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

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class ChangesProcessor {

    private static final Map<Class<? extends IAttributeHandler>, IAttributeHandler> REGISTRY;

    static {
        REGISTRY = new HashMap<Class<? extends IAttributeHandler>, IAttributeHandler>();
    }

    static {
        addAttributeHandler(AspectNameHandler.class);
        addAttributeHandler(OutputPortHandler.class);
        addAttributeHandler(PerformerHandler.class);
        addAttributeHandler(RuntimeStateHandler.class);
        addAttributeHandler(ObjectNameHandler.class);
        addAttributeHandler(TitleHandler.class);
        addAttributeHandler(FolderHandler.class);
        addAttributeHandler(VersionHandler.class);
        addAttributeHandler(SysObjectReadOnlyHandler.class);
        addAttributeHandler(UserReadOnlyHandler.class);
        addAttributeHandler(PolicyHandler.class);
        addAttributeHandler(UserPermitHandler.class);
        addAttributeHandler(PersistentHandler.class);
    }

    private ChangesProcessor() {
        super();
    }

    @SuppressWarnings("unchecked")
    public static void process(final IDfPersistentObject object,
            final Map<String, ?> values) throws DfException {
        Map<String, Object> data = prepare(object, values);
        Queue<IAttributeHandler> handlers = getHandlers(object, data);
        while (!handlers.isEmpty()) {
            IAttributeHandler handler = handlers.remove();
            if (handler.apply(object, data)) {
                handlers.offer(handler);
            }
        }
        IAttributeHandler handler = getAttributeHandler(PersistentHandler.class);
        handler.apply(object, data);
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

}
