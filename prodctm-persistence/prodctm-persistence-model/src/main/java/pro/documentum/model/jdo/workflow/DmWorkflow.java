package pro.documentum.model.jdo.workflow;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import pro.documentum.model.jdo.AbstractPersistent;
import pro.documentum.model.jdo.embedded.workflow.ParentWorkflow;
import pro.documentum.model.jdo.sysobject.DmProcess;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(table = "dm_workflow")
@Accessors(chain = true)
public class DmWorkflow extends AbstractPersistent {

    @Column(name = "object_name")
    @Getter
    private String objectName;

    @Column(name = "process_id")
    @Getter
    private DmProcess process;

    @Column(name = "supervisor_name")
    @Getter
    private String supervisor;

    @Column(name = "initiate_act")
    @Getter
    private String initiateActivity;

    @Column(name = "correlation_identifier")
    @Getter
    private String correlationIdentifier;

    @Embedded(members = {
        @Persistent(name = "workflowId", columns = @Column(name = "parent_id")),
        @Persistent(name = "activityName", columns = @Column(name = "parent_act_name")),
        @Persistent(name = "activitySeqNo", columns = @Column(name = "parent_act_seqno")) })
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    @Setter
    private ParentWorkflow parentWorkflow;

}
