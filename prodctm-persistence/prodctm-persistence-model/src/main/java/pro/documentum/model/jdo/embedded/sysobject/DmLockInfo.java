package pro.documentum.model.jdo.embedded.sysobject;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(embeddedOnly = "true", detachable = "true")
@Accessors(chain = true)
public class DmLockInfo {

    @Persistent
    @Getter
    @Setter
    private String lockOwner;

    @Persistent
    @Getter
    @Setter
    private String lockMachine;

    @Persistent
    @Getter
    @Setter
    private Date lockDate;

}
