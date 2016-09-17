package pro.documentum.configservice;

import com.documentum.fc.client.IDfSession;
import com.documentum.services.config.IConfigElement;
import com.documentum.services.config.IContext;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public interface IConfigProvider {

    String getRootFolderPath();

    String getAppName();

    long getRefreshInterval();

    IDfSession getSession();

    String lookupString(IDfSession session, String path, IContext context);

    boolean lookupBoolean(IDfSession session, String path, IContext context);

    boolean lookupBoolean(IDfSession session, String path, IContext context,
            boolean defaultValue);

    IConfigElement lookupElement(IDfSession session, String path,
            IContext context);

}
