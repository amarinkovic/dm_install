package pro.documentum.model.jpa.embedded.sysobject;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@Embeddable
@Accessors(chain = true)
public class DmLockInfo {

    @Column(name = "r_lock_owner")
    @Getter
    @Setter
    private String lockOwner;

    @Column(name = "r_lock_machine")
    @Getter
    @Setter
    private String lockMachine;

    @Column(name = "r_lock_date")
    @Getter
    @Setter
    private Date lockDate;

}
