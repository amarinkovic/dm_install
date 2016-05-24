package pro.documentum.model.jdo;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import lombok.Getter;
import lombok.Setter;

import org.datanucleus.metadata.MetaData;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = "true")
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "i_vstamp", extensions = {@Extension(vendorName = MetaData.VENDOR_NAME, key = MetaData.EXTENSION_CLASS_VERSION_FIELD_NAME, value = "vStamp") })
@DatastoreIdentity(strategy = IdGeneratorStrategy.INCREMENT, column = "r_object_id")
@Inheritance(strategy = InheritanceStrategy.COMPLETE_TABLE)
public abstract class AbstractPersistent {

    @Column(name = "i_vstamp")
    @Getter
    @Setter
    private int vStamp;

    @Column(name = "r_object_id")
    @Getter
    @Setter
    private String objectId;

}
