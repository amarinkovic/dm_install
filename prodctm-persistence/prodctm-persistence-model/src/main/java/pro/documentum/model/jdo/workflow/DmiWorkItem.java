package pro.documentum.model.jdo.workflow;

import java.util.Date;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;

import lombok.Getter;
import lombok.experimental.Accessors;

import pro.documentum.model.jdo.AbstractPersistent;
import pro.documentum.model.jdo.sysobject.DmActivity;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(table = "dmi_workitem")
@Accessors(chain = true)
public class DmiWorkItem extends AbstractPersistent {

    @Column(name = "r_workflow_id")
    @Getter
    private DmWorkflow workflow;

    @Column(name = "r_act_seqno")
    @Getter
    private int activitySeqNo;

    @Column(name = "r_performer_name")
    @Getter
    private int performerName;

    @Column(name = "r_creation_date")
    @Getter
    private Date creationDate;

    @Column(name = "r_due_date")
    @Getter
    private Date dueDate;

    @Column(name = "r_priority")
    @Getter
    private int priority;

    @Column(name = "r_runtime_state")
    @Getter
    private int runtimeState;

    @Column(name = "r_queue_item_id")
    @Getter
    private DmiQueueItem queueItem;

    @Column(name = "r_act_def_id")
    @Getter
    private DmActivity activity;

}
