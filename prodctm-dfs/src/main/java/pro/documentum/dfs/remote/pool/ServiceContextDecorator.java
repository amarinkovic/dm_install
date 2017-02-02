package pro.documentum.dfs.remote.pool;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.Cookie;

import com.emc.documentum.fs.datamodel.core.context.Identity;
import com.emc.documentum.fs.datamodel.core.context.OverridePermission;
import com.emc.documentum.fs.datamodel.core.context.ServiceContext;
import com.emc.documentum.fs.datamodel.core.profiles.Profile;
import com.emc.documentum.fs.datamodel.core.properties.Property;
import com.emc.documentum.fs.datamodel.core.properties.PropertySet;
import com.emc.documentum.fs.rt.context.IContextHolder;
import com.emc.documentum.fs.rt.context.IServiceContext;
import com.emc.documentum.fs.rt.context.impl.ServiceContextAdapter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class ServiceContextDecorator extends ServiceContextAdapter
        implements IServiceContextDecorator {

    private ServiceContextAdapter _wrapped;

    private ServiceContextDecorator(final IServiceContext context) {
        // fake data to prevent NPE
        super(new ServiceContext());
        _wrapped = (ServiceContextAdapter) context;
    }

    private ServiceContextDecorator() {
        this(null);
    }

    static IServiceContextDecorator of(final IContextHolder service) {
        IServiceContext context = service.getServiceContext();
        if (context instanceof IServiceContextDecorator) {
            return (IServiceContextDecorator) context;
        }
        return null;
    }

    static IServiceContext wrap(final IServiceContext context) {
        Class<?> cls = context.getClass();
        for (Method mtd : cls.getDeclaredMethods()) {
            int modifiers = mtd.getModifiers();
            if (!Modifier.isPublic(modifiers)) {
                continue;
            }
            if (!Modifier.isFinal(modifiers)) {
                continue;
            }
            throw new RuntimeException("Method " + mtd.getName() + " of class "
                    + cls.getName() + " has a final modifier");
        }

        return new ServiceContextDecorator(context);
    }

    public void setWrapped(final IServiceContext wrapped) {
        _wrapped = (ServiceContextAdapter) wrapped;
    }

    public IServiceContext unwrap() {
        return _wrapped;
    }

    @Override
    public String getLocale() {
        return _wrapped.getLocale();
    }

    @Override
    public void setLocale(final String locale) {
        _wrapped.setLocale(locale);
    }

    @Override
    public void setDelta(final ServiceContext context) {
        _wrapped.setDelta(context);
    }

    @Override
    public String getToken() {
        return _wrapped.getToken();
    }

    @Override
    public void setToken(final String token) {
        _wrapped.setToken(token);
    }

    @Override
    public List<Identity> getIdentityList() {
        return _wrapped.getIdentityList();
    }

    @Override
    public Iterator<Identity> getIdentities() {
        return _wrapped.getIdentities();
    }

    @Override
    public void setIdentities(final List<Identity> identities) {
        _wrapped.setIdentities(identities);
    }

    @Override
    public Iterator<Identity> getIdentityPerRepositoryIterator() {
        return _wrapped.getIdentityPerRepositoryIterator();
    }

    @Override
    public int getIdentityCount() {
        return _wrapped.getIdentityCount();
    }

    @Override
    public Identity getIdentity(final int i) {
        return _wrapped.getIdentity(i);
    }

    @Override
    public void addIdentity(final Identity identity) {
        _wrapped.addIdentity(identity);
    }

    @Override
    public void addIdentityBypassingTokenGeneration(final Identity identity) {
        _wrapped.addIdentityBypassingTokenGeneration(identity);
    }

    @Override
    public <T> void setRuntimeProperty(final String name, final T value) {
        _wrapped.setRuntimeProperty(name, value);
    }

    @Override
    public Iterator<Property> getRuntimeProperties() {
        return _wrapped.getRuntimeProperties();
    }

    @Override
    public void setRuntimeProperties(final PropertySet properties) {
        _wrapped.setRuntimeProperties(properties);
    }

    @Override
    public Property getRuntimeProperty(final String name) {
        return _wrapped.getRuntimeProperty(name);
    }

    @Override
    public Iterator<Profile> getProfiles() {
        return _wrapped.getProfiles();
    }

    @Override
    public void setProfiles(final List<Profile> profiles) {
        _wrapped.setProfiles(profiles);
    }

    @Override
    public int getProfileCount() {
        return _wrapped.getProfileCount();
    }

    @Override
    public void setProfile(final Profile profile) {
        _wrapped.setProfile(profile);
    }

    @Override
    public <T extends Profile> T getProfile(final Class<T> profileClass) {
        return _wrapped.getProfile(profileClass);
    }

    @Override
    public OverridePermission getOverridePermission() {
        return _wrapped.getOverridePermission();
    }

    @Override
    public void setOverridePermission(
            final OverridePermission overridePermission) {
        _wrapped.setOverridePermission(overridePermission);
    }

    @Override
    public boolean remoteRegistrationFailed() {
        return _wrapped.remoteRegistrationFailed();
    }

    @Override
    public void setRemotelyRegistred(final boolean forceLocal) {
        _wrapped.setRemotelyRegistred(forceLocal);
    }

    /*
     * This method is marked as final in ServiceContextAdapter, so, you need to
     * modify bytecode of ServiceContextAdapter before compiling this method.
     * Example: ClassPool pool = ClassPool.getDefault(); String cls =
     * "com.emc.documentum.fs.rt.context.impl.ServiceContextAdapter"; CtClass cc
     * = pool.get(cls); for (CtMethod method : cc.getMethods()) { if
     * (!"getDeltaContext".equals(method.getName())) { continue; } int modifiers
     * = method.getModifiers(); if
     * (!com.documentum.thirdparty.javassist.Modifier.isFinal(modifiers)) {
     * continue; }
     * method.setModifiers(com.documentum.thirdparty.javassist.Modifier
     * .clear(modifiers, com.documentum.thirdparty.javassist.Modifier.FINAL)); }
     * File temp = File.createTempFile("ServiceContextAdapter.class", "");
     * FileOutputStream fos = new FileOutputStream(temp);
     * fos.write(cc.toBytecode()); fos.close();
     */
    @Override
    public ServiceContext getDeltaContext() {
        return _wrapped.getDeltaContext();
    }

    @Override
    public void clearDeltaContext() {
        _wrapped.clearDeltaContext();
    }

    @Override
    public boolean isDeltaEmpty() {
        return _wrapped.isDeltaEmpty();
    }

    @Override
    public ServiceContext getConsolidatedContext() {
        return _wrapped.getConsolidatedContext();
    }

    @Override
    public void mergeDelta(final ServiceContext deltaContext) {
        _wrapped.mergeDelta(deltaContext);
    }

    @Override
    public boolean isRemoteInvocation() {
        return _wrapped.isRemoteInvocation();
    }

    @Override
    public void setRemoteInvocation(final boolean isRemoteInvocation) {
        _wrapped.setRemoteInvocation(isRemoteInvocation);
    }

    @Override
    public Collection<Cookie> getCookies() {
        return _wrapped.getCookies();
    }

    @Override
    public Cookie getCookie(final String name) {
        return _wrapped.getCookie(name);
    }

    @Override
    public void addCookies(final Collection<Cookie> cookies) {
        _wrapped.addCookies(cookies);
    }

    @Override
    public ServiceContextAdapter clone() {
        _wrapped = _wrapped.clone();
        return this;
    }

    @Override
    public boolean isTransient() {
        return _wrapped.isTransient();
    }

    @Override
    public void generateTemporaryToken() {
        _wrapped.generateTemporaryToken();
    }

}
