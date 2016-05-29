package pro.documentum.model.jpa;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.datanucleus.api.jpa.annotations.DatastoreId;
import org.datanucleus.api.jpa.annotations.ValueGenerator;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DatastoreId(column = "r_object_id")
@Accessors(chain = true)
public abstract class AbstractPersistent {

    @ValueGenerator(strategy = "increment")
    @Column(name = "r_object_id")
    @Getter
    @Setter
    private String objectId;

    @Version
    @Column(name = "i_vstamp")
    @Getter
    @Setter
    private int vStamp;

}
