package pro.documentum.configservice;

import org.junit.Test;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class RepositoryConfigProviderTest extends AbstractConfigServiceTest {

    @Test
    public void testBaseQualifier() throws Exception {
        RepositoryConfigProvider provider = RepositoryConfigProvider
                .getInstance(FOLDER, APP);
        assertNotNull(provider);
        boolean value = provider.lookupBoolean(getSession(), "test_value",
                new BaseContext());
        assertFalse(value);
        value = provider.lookupBoolean(getSession(), "test_value",
                new BaseContext().add("type", "dm_sysobject"));
        assertTrue(value);
        value = provider.lookupBoolean(getSession(), "test_value",
                new BaseContext().add("type", "dm_document"));
        assertTrue(value);
        value = provider.lookupBoolean(getSession(), "test_value",
                new BaseContext().add("type", "dm_folder"));
        assertFalse(value);
    }

}
