package pro.documentum.model.jdo.embedded.workflow;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(embeddedOnly = "true", detachable = "true")
@Accessors(chain = true)
public class PackageNote {

    @Persistent
    @Getter
    private String noteWriter;

    @Persistent
    @Getter
    private String noteId;

    @Persistent
    @Getter
    private String packageId;

    @Persistent
    @Getter
    private int noteFlag;

}
