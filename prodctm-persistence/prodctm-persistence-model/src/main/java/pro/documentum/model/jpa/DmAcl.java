package pro.documentum.model.jpa;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.datanucleus.api.jpa.annotations.DatastoreId;

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

    @Column(name = "r_accessor_name")
    @Getter
    @Setter
    private List<String> accessorNames;

    @Column(name = "r_accessor_permit")
    @Getter
    @Setter
    private List<Integer> accessorPermits;

    @Column(name = "r_accessor_xpermit")
    @Getter
    @Setter
    private List<Integer> accessorXPermits;

    @Column(name = "r_is_group")
    @Getter
    @Setter
    private List<Boolean> group;

    @Column(name = "r_permit_type")
    @Getter
    @Setter
    private List<Integer> permitTypes;

    @Column(name = "r_application_permit")
    @Getter
    @Setter
    private List<String> applicationPermits;

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
