package pro.documentum.model.jpa.acl;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.datanucleus.api.jpa.annotations.DatastoreId;

import pro.documentum.model.jpa.AbstractPersistent;
import pro.documentum.model.jpa.embedded.acl.DmPermit;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@Entity
@Table(name = "dm_acl")
@DatastoreId
@Accessors(chain = true)
public class DmAcl extends AbstractPersistent {

    @Column(name = "object_name")
    @Getter
    @Setter
    private String objectName;

    @Column(name = "description")
    @Getter
    @Setter
    private String description;

    @Column(name = "owner_name")
    @Getter
    @Setter
    private String ownerName;

    @Column(name = "globally_managed")
    @Getter
    @Setter
    private boolean globallyManaged;

    @Column(name = "acl_class")
    @Getter
    @Setter
    private int aclClass;

    @Column(name = "r_is_internal")
    @Getter
    @Setter
    private boolean internal;

    @Embedded
    @Getter
    @Setter
    private List<DmPermit> permits;

    @Column(name = "r_has_events")
    @Getter
    private boolean hasEvents;

    @Column(name = "r_template_id")
    @Getter
    private String templateId;

    @Column(name = "r_alias_set_id")
    @Getter
    private String aliasSetId;

}
