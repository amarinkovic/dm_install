package pro.documentum.model.jpa;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

import lombok.Getter;
import lombok.experimental.Accessors;

import org.datanucleus.api.jpa.annotations.DatastoreId;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DatastoreId(column = "r_object_id")
@Accessors(chain = true)
public abstract class AbstractPersistent {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "r_object_id")
    @Getter
    private String objectId;

    @Version
    @Column(name = "i_vstamp")
    @Getter
    private int vStamp;

    @Column(name = "i_is_replica")
    @Getter
    private boolean replica;

    @Transient
    protected Object[] dnDetachedState;

}
