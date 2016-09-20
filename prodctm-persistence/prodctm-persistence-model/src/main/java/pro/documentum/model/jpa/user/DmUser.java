package pro.documentum.model.jpa.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.datanucleus.api.jpa.annotations.DatastoreId;

import pro.documentum.model.jpa.AbstractPersistent;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@Entity
@Table(name = "dm_user")
@DatastoreId
@Accessors(chain = true)
public class DmUser extends AbstractPersistent {

    @Column(name = "user_name")
    @Getter
    @Setter
    private String userName;

    @Column(name = "user_os_name")
    @Getter
    @Setter
    private String userOSName;

    @Column(name = "user_address")
    @Getter
    @Setter
    private String userAddress;

    @Column(name = "user_group_name")
    @Getter
    @Setter
    private String userGroupName;

    @Column(name = "user_privileges")
    @Getter
    @Setter
    private int userPrivileges;

    @Column(name = "default_folder")
    @Getter
    @Setter
    private String defaultFolder;

    @Column(name = "user_db_name")
    @Getter
    @Setter
    private String userDBName;

    @Column(name = "description")
    @Getter
    @Setter
    private String description;

    @Column(name = "user_login_name")
    @Getter
    @Setter
    private String userLoginName;

    @Column(name = "r_is_group")
    @Getter
    private boolean group;

    @Column(name = "r_modify_date")
    @Getter
    private Date modifyDate;

    @Column(name = "owner_def_permit")
    @Getter
    @Setter
    private int ownerDefPermit;

    @Column(name = "group_def_permit")
    @Getter
    @Setter
    private int groupDefPermit;

    @Column(name = "world_def_permit")
    @Getter
    @Setter
    private int worldDefPermit;

}
