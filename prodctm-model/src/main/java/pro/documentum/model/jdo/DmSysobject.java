package pro.documentum.model.jdo;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(table = "dm_sysobject")
@Accessors(chain = true)
public class DmSysobject extends AbstractPersistent {

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
    private DmSysobject chronicle;

    @Column(name = "i_folder_id")
    @Element(types = {DmFolder.class, })
    @Setter
    @Getter
    private List<DmFolder> folders;

    @Column(name = "r_version_label")
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    @Setter
    private List<String> versionLabels;

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

    @Column
    @Getter
    @Setter
    private DmAcl acl;

    @Column(name = "r_lock_owner")
    @Getter
    @Setter
    private String lockOwner;

}
