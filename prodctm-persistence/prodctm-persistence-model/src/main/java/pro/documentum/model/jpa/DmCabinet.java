package pro.documentum.model.jpa;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.experimental.Accessors;

import org.datanucleus.api.jpa.annotations.DatastoreId;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@Entity
@Table(name = "dm_cabinet")
@DatastoreId
@Accessors(chain = true)
public class DmCabinet extends DmFolder {

}
