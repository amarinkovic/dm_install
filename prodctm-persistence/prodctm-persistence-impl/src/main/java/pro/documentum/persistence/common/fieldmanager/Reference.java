package pro.documentum.persistence.common.fieldmanager;

import java.util.Objects;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class Reference {

    private final String _columnName;

    private final String _targetColumn;

    public Reference(final String columnName, final String targetColumn) {
        _columnName = columnName;
        _targetColumn = targetColumn;
    }

    public String getColumnName() {
        return _columnName;
    }

    public String getTargetColumn() {
        return _targetColumn;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Reference reference = (Reference) o;
        return Objects.equals(_columnName, reference._columnName)
                && Objects.equals(_targetColumn, reference._targetColumn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_columnName, _targetColumn);
    }

}
