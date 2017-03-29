package pro.documentum.jmx;

import com.documentum.fc.common.IDfMBeanServerProvider;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class EmcMBeanServerProvider extends AbstractMBeanServerProvider {

    public EmcMBeanServerProvider() {
        super();
    }

    @Override
    protected IDfMBeanServerProvider createProvider() {
        return new com.documentum.fc.common.impl.preferences.EmcMBeanServerProvider();
    }

}
