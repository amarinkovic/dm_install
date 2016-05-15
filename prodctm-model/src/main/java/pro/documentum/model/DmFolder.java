package pro.documentum.model;

import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import lombok.Getter;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(table = "dm_folder")
public class DmFolder extends DmSysobject {

    @Column(name = "r_folder_path")
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    private List<String> folderPaths;

}
