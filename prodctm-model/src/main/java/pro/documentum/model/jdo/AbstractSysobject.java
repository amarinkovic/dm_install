package pro.documentum.model.jdo;

import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import pro.documentum.model.jdo.embedded.DmLockInfo;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(table = "dm_sysobject")
@Accessors(chain = true)
public abstract class AbstractSysobject extends AbstractPersistent {

    @Column(name = "r_object_type")
    @Getter
    private String objectType;

    @Column(name = "r_aspect_name")
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    @Setter
    private List<String> aspectNames;

    @Embedded(members = {
        @Persistent(name = "lockOwner", columns = @Column(name = "r_lock_owner")),
        @Persistent(name = "lockMachine", columns = @Column(name = "r_lock_machine")),
        @Persistent(name = "lockDate", columns = @Column(name = "r_lock_date")) })
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    @Setter
    private DmLockInfo lockInfo;

    @Column(name = "i_is_reference")
    @Getter
    private boolean reference;

}
