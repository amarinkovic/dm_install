package pro.documentum.model.jpa.embedded.acl;

import javax.jdo.annotations.Persistent;
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
public class DmPermit {

    @Column(name = "r_accessor_name")
    @Getter
    @Setter
    private String accessorName;

    @Column(name = "r_accessor_permit")
    @Persistent
    @Getter
    @Setter
    private int accessorPermit;

    @Column(name = "r_accessor_xpermit")
    @Getter
    @Setter
    private int accessorXPermit;

    @Column(name = "r_is_group")
    @Getter
    @Setter
    private boolean group;

    @Column(name = "r_permit_type")
    @Getter
    @Setter
    private int permitType;

    @Column(name = "r_application_permit")
    @Getter
    @Setter
    private String applicationPermit;

}
