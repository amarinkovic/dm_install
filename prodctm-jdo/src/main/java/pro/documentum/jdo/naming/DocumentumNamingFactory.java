package pro.documentum.jdo.naming;

import org.datanucleus.NucleusContext;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.store.schema.naming.AbstractNamingFactory;
import org.datanucleus.store.schema.naming.ColumnType;
import org.datanucleus.store.schema.naming.SchemaComponent;

import com.documentum.fc.common.DfDocbaseConstants;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DocumentumNamingFactory extends AbstractNamingFactory {

    public DocumentumNamingFactory(final NucleusContext nucCtx) {
        super(nucCtx);
    }

    public String getTableName(final AbstractMemberMetaData mmd) {
        String name = null;
        AbstractMemberMetaData[] relatedMmds = null;
        if (mmd.hasContainer()) {
            if (mmd.getTable() != null) {
                name = mmd.getTable();
                // TODO This may have "catalog.schema.name"
            } else {
                relatedMmds = mmd.getRelatedMemberMetaData(clr);
                if (relatedMmds != null && relatedMmds[0].getTable() != null) {
                    name = relatedMmds[0].getTable();
                    // TODO This may have "catalog.schema.name"
                }
            }
        }
        if (name == null) {
            String ownerClass = mmd.getClassName(false);
            name = ownerClass + wordSeparator + mmd.getName();
        }

        // Apply any truncation/casing necessary
        return prepareIdentifierNameForUse(name, SchemaComponent.TABLE);
    }

    @Override
    public String getColumnName(final AbstractClassMetaData cmd,
            final ColumnType type) {
        String name = null;
        if (type == ColumnType.DISCRIMINATOR_COLUMN) {
            name = cmd.getDiscriminatorColumnName();
            if (name == null) {
                name = DfDocbaseConstants.R_OBJECT_ID;
            }
        } else if (type == ColumnType.VERSION_COLUMN) {
            return DfDocbaseConstants.I_VSTAMP;
        } else if (type == ColumnType.DATASTOREID_COLUMN) {
            if (cmd.getIdentityMetaData() != null) {
                ColumnMetaData idcolmds = cmd.getIdentityMetaData()
                        .getColumnMetaData();
                if (idcolmds != null) {
                    name = idcolmds.getName();
                }
            }
            if (name == null) {
                name = DfDocbaseConstants.R_OBJECT_ID;
            }
        } else {
            throw new NucleusException(
                    "This method does not support columns of type " + type);
        }

        return prepareIdentifierNameForUse(name, SchemaComponent.COLUMN);
    }

    @Override
    public String getColumnName(final AbstractMemberMetaData mmd,
            final ColumnType type, final int position) {
        String name = null;
        if (type == ColumnType.COLUMN) {
            ColumnMetaData[] colmds = mmd.getColumnMetaData();
            if (colmds != null && colmds.length > position) {
                name = colmds[position].getName();
            }
            if (name == null) {
                name = mmd.getName();
            }
        } else if (type == ColumnType.INDEX_COLUMN) {
            if (mmd.getOrderMetaData() != null) {
                ColumnMetaData[] colmds = mmd.getOrderMetaData()
                        .getColumnMetaData();
                if (colmds != null && colmds.length > position) {
                    name = colmds[position].getName();
                }
            }
            if (name == null) {
                name = "IDX";
            }
        } else if (type == ColumnType.ADAPTER_COLUMN) {
            name = "IDX";
        } else if (type == ColumnType.FK_COLUMN) {
            throw new NucleusException(
                    "This method does not support columns of type " + type);
        } else if (type == ColumnType.JOIN_OWNER_COLUMN) {
            if (mmd.hasContainer()) {
                if (mmd.getJoinMetaData() != null) {
                    ColumnMetaData[] colmds = mmd.getJoinMetaData()
                            .getColumnMetaData();
                    if (colmds != null && colmds.length > position) {
                        name = colmds[position].getName();
                    }
                }
            }
            if (name == null) {
                if (mmd.hasContainer()) {
                    name = mmd.getName() + wordSeparator + "ID_OID";
                }
            }
        } else {
            throw new NucleusException(
                    "This method does not support columns of type " + type);
        }

        return prepareIdentifierNameForUse(name, SchemaComponent.COLUMN);
    }

}
