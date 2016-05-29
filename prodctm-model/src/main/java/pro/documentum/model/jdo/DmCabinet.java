package pro.documentum.model.jdo;

import javax.jdo.annotations.PersistenceCapable;

import lombok.experimental.Accessors;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(table = "dm_cabinet")
@Accessors(chain = true)
public class DmCabinet extends DmFolder {

}
