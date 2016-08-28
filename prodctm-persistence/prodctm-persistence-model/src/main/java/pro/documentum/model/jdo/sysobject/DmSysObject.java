package pro.documentum.model.jdo.sysobject;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Columns;
import javax.jdo.annotations.PersistenceCapable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import pro.documentum.model.jdo.acl.DmAcl;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(table = "dm_sysobject")
@Accessors(chain = true)
public class DmSysObject extends AbstractSysObject {

    @Column(name = "object_name")
    @Getter
    @Setter
    private String objectName;

    @Column(name = "title")
    @Getter
    @Setter
    private String title;

    @Column(name = "subject")
    @Getter
    @Setter
    private String subject;

    @Column(name = "i_chronicle_id")
    @Getter
    private DmSysObject chronicle;

    @Column(name = "i_folder_id")
    @Setter
    @Getter
    private List<DmFolder> folders;

    @Column(name = "r_creation_date")
    @Getter
    private Date creationDate;

    @Column(name = "r_modify_date")
    @Getter
    private Date modifyDate;

    @Column(name = "i_cabinet_id")
    @Getter
    @Setter
    private DmCabinet cabinet;

    @Column(name = "owner_name")
    @Getter
    private String ownerName;

    @Column(name = "r_creator_name")
    @Getter
    private String creatorName;

    @Columns(value = {@Column(name = "acl_name", targetMember = "objectName"),
        @Column(name = "acl_domain", targetMember = "ownerName") })
    @Getter
    @Setter
    private DmAcl acl;

}
