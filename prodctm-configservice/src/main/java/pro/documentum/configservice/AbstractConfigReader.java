package pro.documentum.configservice;

import java.util.ArrayList;
import java.util.List;

import com.documentum.services.config.IConfigFile;
import com.documentum.services.config.IConfigReader;
import com.documentum.services.config.IConfigService;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
abstract class AbstractConfigReader implements IConfigReader {

    private IConfigService _configService;

    private String _rootFolderPath;

    private String _appName;

    protected AbstractConfigReader() {
        super();
    }

    @Override
    public void initialize(final String rootFolderPath, final String appName) {
        _rootFolderPath = rootFolderPath;
        _appName = appName;
    }

    @Override
    public String getAppName() {
        return _appName;
    }

    @Override
    public String getRootFolderPath() {
        return _rootFolderPath;
    }

    @Override
    public IConfigFile loadAppConfigFile(final String appName) {
        return _configService.newConfigFile(getAppConfig());
    }

    protected abstract String getAppConfig();

    @Override
    public Iterable<IConfigFile> loadConfigFiles(final String appName) {
        List<IConfigFile> result = new ArrayList<>();
        List<String> configFiles = getConfigs();
        if (configFiles == null || configFiles.isEmpty()) {
            return result;
        }
        for (String path : configFiles) {
            result.add(_configService.newConfigFile(path));
        }
        return result;
    }

    protected abstract List<String> getConfigs();

    @Override
    public void setConfigService(final IConfigService configService) {
        _configService = configService;
    }

}
