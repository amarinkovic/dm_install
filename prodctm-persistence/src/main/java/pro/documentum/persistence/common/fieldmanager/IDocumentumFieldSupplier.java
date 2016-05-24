package pro.documentum.persistence.common.fieldmanager;

import java.util.Date;

import org.datanucleus.store.fieldmanager.FieldSupplier;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public interface IDocumentumFieldSupplier extends FieldSupplier {

    Date fetchDateField(int fieldNumber);

}
