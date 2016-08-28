package pro.documentum.model.jdo.workflow;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import lombok.Getter;
import lombok.experimental.Accessors;

import pro.documentum.model.jdo.AbstractPersistent;
import pro.documentum.model.jdo.embedded.workflow.PackageComponent;
import pro.documentum.model.jdo.embedded.workflow.PackageNote;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
@PersistenceCapable(table = "dmi_package")
@Accessors(chain = true)
public class DmiPackage extends AbstractPersistent {

    @Column(name = "r_workflow_id")
    @Getter
    private DmWorkflow workflow;

    @Column(name = "r_act_seqno")
    @Getter
    private int activitySeqNo;

    @Column(name = "r_port_name")
    @Getter
    private String portName;

    @Column(name = "r_package_name")
    @Getter
    private String packageName;

    @Column(name = "r_package_type")
    @Getter
    private String packageType;

    @Column(name = "r_package_flag")
    @Getter
    private String packageFlag;

    @Element(types = {PackageComponent.class, }, embedded = "true", embeddedMapping = {@Embedded(members = {
        @Persistent(name = "componentId", columns = @Column(name = "r_component_id")),
        @Persistent(name = "componentChronicleId", columns = @Column(name = "r_component_chron_id")),
        @Persistent(name = "componentName", columns = @Column(name = "r_component_name")),
        @Persistent(name = "packageLabel", columns = @Column(name = "r_package_label")), }) })
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    private List<PackageComponent> components;

    @Element(types = {PackageNote.class, }, embedded = "true", embeddedMapping = {@Embedded(members = {
        @Persistent(name = "noteWriter", columns = @Column(name = "r_note_writer")),
        @Persistent(name = "noteId", columns = @Column(name = "r_note_id")),
        @Persistent(name = "packageId", columns = @Column(name = "r_package_id")),
        @Persistent(name = "noteFlag", columns = @Column(name = "r_note_flag")), }) })
    @Persistent(defaultFetchGroup = "true", serialized = "true")
    @Getter
    private List<PackageNote> notes;

    @Column(name = "i_package_order")
    @Getter
    private int packageOrder;

    @Column(name = "i_package_oprtn")
    @Getter
    private String packageOprtn;

    @Column(name = "i_acceptance_date")
    @Getter
    private Date acceptanceDate;

}
