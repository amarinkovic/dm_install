package pro.documentum.model.jdo.workflow;

import javax.jdo.annotations.PersistenceCapable;

import lombok.experimental.Accessors;

import pro.documentum.model.jdo.AbstractPersistent;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(table = "dmi_queue_item")
@Accessors(chain = true)
public class DmiQueueItem extends AbstractPersistent {

}
