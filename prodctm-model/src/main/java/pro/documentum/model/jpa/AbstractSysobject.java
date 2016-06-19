package pro.documentum.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.experimental.Accessors;

import org.datanucleus.api.jpa.annotations.DatastoreId;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@Entity
@Table(name = "dm_sysobject")
@DatastoreId
@Accessors(chain = true)
public class AbstractSysobject extends AbstractPersistent {

    @Column(name = "r_object_type")
    @Getter
    private String objectType;

    @Column(name = "i_is_reference")
    @Getter
    private boolean reference;

}
