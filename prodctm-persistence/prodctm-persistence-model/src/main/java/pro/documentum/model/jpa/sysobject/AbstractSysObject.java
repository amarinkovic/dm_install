package pro.documentum.model.jpa.sysobject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.datanucleus.api.jpa.annotations.DatastoreId;

import pro.documentum.model.jpa.AbstractPersistent;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@Entity
@Table(name = "dm_sysobject")
@DatastoreId
@Accessors(chain = true)
public class AbstractSysObject extends AbstractPersistent {

    @Column(name = "r_object_type")
    @Getter
    private String objectType;

    @Column(name = "i_is_reference")
    @Getter
    private boolean reference;

    @Column(name = "a_content_type")
    @Getter
    @Setter
    private String contentType;

}
