package pro.documentum.composer.tasks;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class Artifact {

    private String _name;

    private String _category;

    private String _value;

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

    public String getValue() {
        return _value;
    }

    public void setValue(final String value) {
        _value = value;
    }

    public void setIgnoreReadOnly(final String ignoreReadOnly) {
        _ignoreReadOnly = Boolean.valueOf(ignoreReadOnly);
    }

    public boolean isIgnoreReadOnly() {
        return _ignoreReadOnly;
    }

}
