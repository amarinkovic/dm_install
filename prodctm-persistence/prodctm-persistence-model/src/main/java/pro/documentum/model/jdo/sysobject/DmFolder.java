package pro.documentum.model.jdo.sysobject;

import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(table = "dm_folder")
@Accessors(chain = true)
public class DmFolder extends DmSysObject {

    @Column(name = "r_folder_path")
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    private List<String> folderPaths;

}
