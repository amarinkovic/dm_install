package pro.documentum.model.jdo;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(table = "dm_sysobject")
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
    private String chronicleId;

    @Column(name = "i_folder_id")
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    @Setter
    private List<String> folderIds;

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
    private String cabinetId;

    @Column(name = "owner_name")
    @Getter
    private String ownerName;

    @Column(name = "r_creator_name")
    @Getter
    private String creatorName;

    @Column(name = "acl_name")
    @Getter
    @Setter
    private String aclName;

    @Column(name = "acl_domain")
    @Getter
    @Setter
    private String aclDomain;

    @Column(name = "r_lock_owner")
    @Getter
    @Setter
    private String lockOwner;

}
