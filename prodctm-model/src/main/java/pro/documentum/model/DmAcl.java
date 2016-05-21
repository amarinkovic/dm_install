package pro.documentum.model;

import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(table = "dm_acl")
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
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    @Setter
    private String[] accessorNames;

    @Column(name = "r_accessor_permit")
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    @Setter
    private int[] accessorPermits;

    @Column(name = "r_accessor_xpermit")
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    @Setter
    private Integer[] accessorXPermits;

    @Column(name = "r_is_group")
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    @Setter
    private boolean[] group;

    @Column(name = "r_permit_type")
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    @Setter
    private List<Integer> permitTypes;

    @Column(name = "r_application_permit")
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    @Setter
    private List<String> applicationPermits;

    @Column(name = "r_has_events")
    @Getter
    @Setter
    private boolean hasEvents;

    @Column(name = "r_template_id")
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    @Setter
    private String templateId;

    @Column(name = "r_alias_set_id")
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    @Setter
    private String aliasSetId;

}
