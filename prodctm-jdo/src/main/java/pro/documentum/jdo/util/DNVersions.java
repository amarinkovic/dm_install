package pro.documentum.jdo.util;

import org.datanucleus.ExecutionContext;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.VersionMetaData;
import org.datanucleus.state.ObjectProvider;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DNVersions {

    private DNVersions() {
        super();
    }

    private static int getVersionPosition(final ObjectProvider<?> op) {
        AbstractClassMetaData cmd = op.getClassMetaData();
        if (!cmd.isVersioned()) {
            return -1;
        }
        VersionMetaData vermd = cmd.getVersionMetaDataForClass();
        String fieldName = vermd.getFieldName();
        if (fieldName != null) {
            return cmd.getMetaDataForMember(fieldName).getAbsoluteFieldNumber();
        }
        return -1;
    }

    public static void processVersion(final ObjectProvider<?> op) {
        int versionPosition = getVersionPosition(op);
        if (versionPosition > -1) {
            Object version = op.provideField(versionPosition);
            op.setVersion(version);
        }
    }

    public static void processVersion(final ExecutionContext ec, final Object pc) {
        processVersion(ec.findObjectProvider(pc));
    }

}
