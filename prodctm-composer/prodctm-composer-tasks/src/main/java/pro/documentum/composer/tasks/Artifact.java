package pro.documentum.composer.tasks;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class Artifact {

    private String _name;

    private String _category;

    private String _upgradeOption;

    private String _contentPath;

    private boolean _ignoreReadOnly;

    public Artifact() {
        super();
    }

    public String getName() {
        return _name;
    }

    public void setName(final String name) {
        _name = name;
    }

    public String getCategory() {
        return _category;
    }

    public void setCategory(final String category) {
        _category = category;
    }

    public String getUpgradeOption() {
        return _upgradeOption;
    }

    public void setUpgradeOption(final String upgradeOption) {
        _upgradeOption = upgradeOption;
    }

    public void setIgnoreReadOnly(final String ignoreReadOnly) {
        _ignoreReadOnly = Boolean.valueOf(ignoreReadOnly);
    }

    public boolean isIgnoreReadOnly() {
        return _ignoreReadOnly;
    }

    public String getContentPath() {
        return _contentPath;
    }

    public void setContentPath(final String contentPath) {
        _contentPath = contentPath;
    }

}
