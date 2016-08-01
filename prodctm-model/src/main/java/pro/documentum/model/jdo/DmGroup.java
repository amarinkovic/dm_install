package pro.documentum.model.jdo;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Serialized;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(table = "dm_group")
@Accessors(chain = true)
public class DmGroup extends AbstractPersistent {

    @Column(name = "group_name")
    @Getter
    @Setter
    private String groupName;

    @Column(name = "group_address")
    @Getter
    @Setter
    private String groupAddress;

    @Column(name = "users_names", allowsNull = "false")
    @Serialized
    @Getter
    @Setter
    private List<String> usersNames;

    @Column(name = "groups_names", allowsNull = "false")
    @Serialized
    @Getter
    @Setter
    private List<String> groupsNames;

    @Column(name = "owner_name")
    @Getter
    @Setter
    private String ownerName;

    @Column(name = "is_private")
    @Getter
    @Setter
    private boolean privateGroup;

    @Column(name = "description")
    @Getter
    @Setter
    private String description;

    @Column(name = "globally_managed")
    @Getter
    @Setter
    private boolean globallyManaged;

    @Column(name = "alias_set_id")
    @Getter
    @Setter
    private String aliasSetId;

    @Column(name = "group_source")
    @Getter
    @Setter
    private String groupSource;

    @Column(name = "group_class")
    @Getter
    @Setter
    private String groupClass;

    @Column(name = "group_admin")
    @Getter
    @Setter
    private String groupAdmin;

    @Column(name = "is_dynamic")
    @Getter
    @Setter
    private boolean dynamic;

    @Column(name = "is_dynamic_default")
    @Getter
    @Setter
    private boolean dynamicDefault;

    @Column(name = "group_global_unique_id")
    @Getter
    @Setter
    private String globalUniqueId;

    @Column(name = "group_native_room_id")
    @Getter
    @Setter
    private String nativeRoomId;

    @Column(name = "group_directory_id")
    @Getter
    @Setter
    private String directoryId;

    @Column(name = "group_display_name")
    @Getter
    @Setter
    private String displayName;

    @Column(name = "is_protected")
    @Getter
    @Setter
    private boolean protectedGroup;

    @Column(name = "is_module_only")
    @Getter
    @Setter
    private boolean moduleOnly;

    @Column(name = "r_modify_date")
    @Getter
    private Date modifyDate;

    @Column(name = "r_has_events")
    @Getter
    private boolean hasEvents;

    @Column(name = "i_all_users_names")
    @Getter
    private List<String> allUsersNames;

    @Column(name = "i_supergroups_names")
    @Getter
    private List<String> superGroupsNames;

    @Column(name = "i_nondyn_supergroups_names")
    @Getter
    private List<String> nonDynSuperGroupsNames;

}
