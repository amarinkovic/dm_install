package pro.documentum.model.jdo.embedded;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(embeddedOnly = "true", detachable = "true")
@Accessors(chain = true)
public class EmbeddedString {

    @Persistent
    @Getter
    @Setter
    private String value;

}
