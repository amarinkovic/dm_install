package pro.documentum.model.jpa.sysobject;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.experimental.Accessors;

import org.datanucleus.api.jpa.annotations.DatastoreId;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@Entity
@Table(name = "dm_folder")
@DatastoreId
@Accessors(chain = true)
public class DmFolder extends DmSysObject {

    @Column(name = "r_folder_path")
    @Getter
    private List<String> folderPaths;

}
