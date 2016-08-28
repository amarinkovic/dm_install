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
public class ParentWorkflow {

    @Persistent
    @Getter
    private String workflowId;

    @Persistent
    @Getter
    private String activityName;

    @Persistent
    @Getter
    private String activitySeqNo;

}
