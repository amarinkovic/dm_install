package pro.documentum.configservice;

import java.io.IOException;
import java.io.InputStream;

import com.documentum.services.config.INlsReader;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */

final class NlsReader implements INlsReader {

    NlsReader() {
        super();
    }

    @Override
    public void initialize(final String s) {

    }

    @Override
    public void addLookupFolder(final String s) {

    }

    @Override
    public InputStream getResourceAsStream(final String resourceName)
        throws IOException {
        String slashedPropName = resourceName.replace('.', '/');
        slashedPropName = slashedPropName.replace('\\', '/');
        InputStream stream = getClass().getResourceAsStream(
                "/" + slashedPropName + ".properties");
        if (stream == null) {
            getClass().getClassLoader().getResourceAsStream(
                    slashedPropName + ".properties");
        }
        if (stream == null) {
            stream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(slashedPropName + ".properties");
        }
        return stream;
    }

}
