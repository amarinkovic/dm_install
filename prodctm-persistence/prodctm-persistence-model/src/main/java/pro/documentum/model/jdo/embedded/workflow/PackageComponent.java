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
public class PackageComponent {

    @Persistent
    @Getter
    private String componentId;

    @Persistent
    @Getter
    private String componentChronicleId;

    @Persistent
    @Getter
    private String componentName;

    @Persistent
    @Getter
    private String packageLabel;

}
