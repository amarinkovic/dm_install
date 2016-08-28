package pro.documentum.model.jdo.sysobject;

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
import pro.documentum.model.jdo.embedded.EmbeddedString;
import pro.documentum.model.jdo.embedded.sysobject.DmLockInfo;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(table = "dm_sysobject")
@Accessors(chain = true)
public abstract class AbstractSysObject extends AbstractPersistent {

    @Column(name = "r_object_type")
    @Getter
    private String objectType;

    @Element(types = {EmbeddedString.class, }, embedded = "true", embeddedMapping = {
            @Embedded(members = {
                    @Persistent(name = "value", columns = @Column(name = "r_version_label"))
            })
    })
    @Persistent(defaultFetchGroup = "true", serialized = "true", embedded = "true")
    @Getter
    @Setter
    private List<EmbeddedString> versionLabels;

    @Element(types = {EmbeddedString.class, }, embedded = "true", embeddedMapping = {
            @Embedded(members = {
                    @Persistent(name = "value", columns = @Column(name = "r_aspect_name"))
            })
    })
    @Persistent(defaultFetchGroup = "true", serialized = "true", embedded = "true")
    @Getter
    @Setter
    private List<EmbeddedString> aspectNames;

    @Embedded(members = {
        @Persistent(name = "lockOwner", columns = @Column(name = "r_lock_owner")),
        @Persistent(name = "lockMachine", columns = @Column(name = "r_lock_machine")),
        @Persistent(name = "lockDate", columns = @Column(name = "r_lock_date"))
    })
    @Persistent(defaultFetchGroup = "true", serialized = "true", embedded = "true")
    @Getter
    @Setter
    private DmLockInfo lockInfo;

    @Column(name = "i_is_reference")
    @Getter
    private boolean reference;

    @Column(name = "a_content_type")
    @Getter
    @Setter
    private String contentType;

    @Column(name = "i_has_folder")
    @Getter
    @Setter
    private boolean hasFolder;

}
