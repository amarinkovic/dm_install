package pro.documentum.persistence.common.fieldmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.state.ObjectProviderFactory;

import com.documentum.fc.common.DfDocbaseConstants;

import pro.documentum.persistence.common.util.DNMetaData;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class FieldHelper {

    private final ExecutionContext _ec;

    private final AbstractClassMetaData _cmd;

    public FieldHelper(final ExecutionContext ec,
            final AbstractClassMetaData cmd) {
        _ec = ec;
        _cmd = cmd;
    }

    protected List<Reference> getReferences(final AbstractMemberMetaData mmd) {
        if (isIdentityMapping(mmd)) {
            Reference reference = new Reference(DNMetaData.getColumnName(mmd),
                    DfDocbaseConstants.R_OBJECT_ID);
            return Collections.singletonList(reference);
        }
        List<Reference> result = new ArrayList<>();
        List<String> names = DNMetaData.getColumnNames(mmd);
        List<String> targets = getTargetColumns(mmd);
        for (int i = 0, n = names.size(); i < n; i++) {
            Reference reference = new Reference(names.get(i), targets.get(i));
            result.add(reference);
        }
        return result;
    }

    protected boolean isIdentityMapping(final AbstractMemberMetaData mmd) {
        ColumnMetaData[] columnMetaDatum = Objects.requireNonNull(DNMetaData
                .getColumnMetaData(mmd));
        if (columnMetaDatum.length != 1) {
            return false;
        }
        ColumnMetaData cmd = columnMetaDatum[0];
        String targetMember = cmd.getTargetMember();
        String targetColumn = cmd.getTarget();
        if (StringUtils.isBlank(targetMember)
                && StringUtils.isBlank(targetColumn)) {
            return true;
        }
        return false;
    }

    protected List<String> getTargetColumns(final AbstractMemberMetaData mmd) {
        Class<?> targetType = DNMetaData.getElementClass(mmd);
        AbstractClassMetaData cmd = getMetaDataForClass(targetType);
        List<String> result = new ArrayList<>();
        ColumnMetaData[] columnMetaData = Objects.requireNonNull(DNMetaData
                .getColumnMetaData(mmd));
        for (ColumnMetaData col : columnMetaData) {
            String targetColumn = col.getTarget();
            if (StringUtils.isNotBlank(targetColumn)) {
                result.add(targetColumn);
                continue;
            }
            String targetMember = col.getTargetMember();
            AbstractMemberMetaData tmmd = cmd
                    .getMetaDataForMember(targetMember);
            result.add(DNMetaData.getColumnName(tmmd));
        }
        return result;
    }

    protected AbstractClassMetaData getMetaDataForClass(final Class<?> cls) {
        if (cls != null) {
            return getMetaDataForClass(cls.getName());
        }
        return null;
    }

    protected AbstractClassMetaData getMetaDataForClass(final String className) {
        return getMetaDataManager().getMetaDataForClass(className,
                getClassLoaderResolver());
    }

    protected ObjectProviderFactory getObjectProviderFactory() {
        return _ec.getNucleusContext().getObjectProviderFactory();
    }

    protected RelationType getRelationType(final AbstractMemberMetaData mmd) {
        ClassLoaderResolver clr = getClassLoaderResolver();
        return mmd.getRelationType(clr);
    }

    protected boolean isEmbedded(final AbstractMemberMetaData mmd) {
        return DNMetaData.isEmbedded(_ec, mmd);
    }

    protected MetaDataManager getMetaDataManager() {
        return _ec.getMetaDataManager();
    }

    protected ClassLoaderResolver getClassLoaderResolver() {
        return _ec.getClassLoaderResolver();
    }

    protected AbstractMemberMetaData getMemberMetadata(final int fieldNumber) {
        return _cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
    }

    public List<String> getAttrs(final AbstractMemberMetaData[] embmmds) {
        List<String> result = new ArrayList<>();
        for (AbstractMemberMetaData embmmd : embmmds) {
            String attrName = DNMetaData.getColumnName(embmmd);
            result.add(attrName);
        }
        return result;
    }

}
