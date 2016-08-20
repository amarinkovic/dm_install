package pro.documentum.persistence.common.util.fields;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IFieldFilter {

    boolean accept(int fieldNumber);

}
