package pro.documentum.model.jdo.acl;

import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import pro.documentum.model.jdo.AbstractPersistent;
import pro.documentum.model.jdo.embedded.acl.DmPermit;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(table = "dm_acl")
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

    @Element(types = {DmPermit.class, }, embedded = "true", embeddedMapping = {@Embedded(members = {
        @Persistent(name = "accessorName", columns = @Column(name = "r_accessor_name")),
        @Persistent(name = "accessorPermit", columns = @Column(name = "r_accessor_permit")),
        @Persistent(name = "accessorXPermit", columns = @Column(name = "r_accessor_xpermit")),
        @Persistent(name = "applicationPermit", columns = @Column(name = "r_application_permit")),
        @Persistent(name = "group", columns = @Column(name = "r_is_group")),
        @Persistent(name = "permitType", columns = @Column(name = "r_permit_type")), }) })
    @Persistent(defaultFetchGroup = "true", serialized = "true", embedded = "true")
    @Getter
    @Setter
    private List<DmPermit> permits;

    @Column(name = "r_has_events")
    @Getter
    private boolean hasEvents;

    @Column(name = "r_template_id")
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    private String templateId;

    @Column(name = "r_alias_set_id")
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    private String aliasSetId;

}
