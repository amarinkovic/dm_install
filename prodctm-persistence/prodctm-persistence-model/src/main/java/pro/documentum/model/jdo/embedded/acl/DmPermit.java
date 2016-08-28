package pro.documentum.model.jdo.embedded.acl;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(embeddedOnly = "true")
@Accessors(chain = true)
public class DmPermit {

    @Persistent
    @Getter
    @Setter
    private String accessorName;

    @Persistent
    @Getter
    @Setter
    private int accessorPermit;

    @Persistent
    @Getter
    @Setter
    private int accessorXPermit;

    @Persistent
    @Getter
    @Setter
    private boolean group;

    @Persistent
    @Getter
    @Setter
    private int permitType;

    @Persistent
    @Getter
    @Setter
    private String applicationPermit;

}
