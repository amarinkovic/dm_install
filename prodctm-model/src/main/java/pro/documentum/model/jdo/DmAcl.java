package pro.documentum.model.jdo;

import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import pro.documentum.model.jdo.embedded.DmPermit;

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
        @Persistent(name = "accessorXPermit", columns = @Column(name = "r_accessor_xpermit")), }) })
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    @Setter
    private List<DmPermit> permits;

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
