package pro.documentum.util.permits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.documentum.fc.client.DfPermit;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfPermit;
import com.documentum.fc.client.impl.security.ExtendedPermitData;
import com.documentum.fc.client.impl.security.PermitData;
import com.documentum.fc.common.DfException;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class PermitConverter {

    public static final Map<String, Integer> PERMITS_STR = new HashMap<>();

    public static final Map<String, Integer> EXTENDED_STR = new HashMap<>();

    static {
        PERMITS_STR.put(IDfACL.DF_PERMIT_NONE_STR.toLowerCase(),
                IDfACL.DF_PERMIT_NONE);
        PERMITS_STR.put(String.valueOf(IDfACL.DF_PERMIT_NONE),
                IDfACL.DF_PERMIT_NONE);
        PERMITS_STR.put(IDfACL.DF_PERMIT_BROWSE_STR.toLowerCase(),
                IDfACL.DF_PERMIT_BROWSE);
        PERMITS_STR.put(String.valueOf(IDfACL.DF_PERMIT_BROWSE),
                IDfACL.DF_PERMIT_BROWSE);
        PERMITS_STR.put(IDfACL.DF_PERMIT_READ_STR.toLowerCase(),
                IDfACL.DF_PERMIT_READ);
        PERMITS_STR.put(String.valueOf(IDfACL.DF_PERMIT_READ),
                IDfACL.DF_PERMIT_READ);
        PERMITS_STR.put(IDfACL.DF_PERMIT_RELATE_STR.toLowerCase(),
                IDfACL.DF_PERMIT_RELATE);
        PERMITS_STR.put(String.valueOf(IDfACL.DF_PERMIT_RELATE),
                IDfACL.DF_PERMIT_RELATE);
        PERMITS_STR.put(IDfACL.DF_PERMIT_VERSION_STR.toLowerCase(),
                IDfACL.DF_PERMIT_VERSION);
        PERMITS_STR.put(String.valueOf(IDfACL.DF_PERMIT_VERSION),
                IDfACL.DF_PERMIT_VERSION);
        PERMITS_STR.put(IDfACL.DF_PERMIT_WRITE_STR.toLowerCase(),
                IDfACL.DF_PERMIT_WRITE);
        PERMITS_STR.put(String.valueOf(IDfACL.DF_PERMIT_WRITE),
                IDfACL.DF_PERMIT_WRITE);
        PERMITS_STR.put(IDfACL.DF_PERMIT_DELETE_STR.toLowerCase(),
                IDfACL.DF_PERMIT_DELETE);
        PERMITS_STR.put(String.valueOf(IDfACL.DF_PERMIT_DELETE),
                IDfACL.DF_PERMIT_DELETE);

        EXTENDED_STR.put("ALL".toLowerCase(), ExtendedPermitData.ALL);
        EXTENDED_STR.put("NONE".toLowerCase(), ExtendedPermitData.NONE);
        EXTENDED_STR.put("DEFAULT".toLowerCase(), ExtendedPermitData.DEFAULT);
        EXTENDED_STR.put("UNSPECIFIED".toLowerCase(),
                ExtendedPermitData.UNSPECIFIED);
    }

    private PermitConverter() {
        super();
    }

    public static int convertAccess(final String accessPermit) {
        if (StringUtils.isBlank(accessPermit)) {
            return PermitData.UNSPECIFIED;
        }
        Integer value = PERMITS_STR.get(accessPermit.toLowerCase());
        if (value == null) {
            return PermitData.UNSPECIFIED;
        }
        return value;
    }

    public static int convertAccess(final int accessPermit) {
        return convertAccess(String.valueOf(accessPermit));
    }

    public static int convertExtended(final String extendedPermit) {
        if (StringUtils.isBlank(extendedPermit)) {
            return ExtendedPermitData.DEFAULT;
        }
        Integer parsed = EXTENDED_STR.get(extendedPermit.toLowerCase());
        if (parsed != null) {
            return parsed;
        }
        return new ExtendedPermitData(extendedPermit).getValue();
    }

    @SuppressWarnings("unchecked")
    public static List<String> convertExtended(final int extendedPermit) {
        if (extendedPermit == ExtendedPermitData.UNSPECIFIED) {
            return null;
        }
        return new ExtendedPermitData(extendedPermit).getNames();
    }

    public static String convertExtended(final ExtendedPermitData data) {
        if (data.getValue() == ExtendedPermitData.UNSPECIFIED) {
            return null;
        }
        return StringUtils.join(data.getNames(), ",");
    }

    private static IDfPermit newPermit(final int permitType,
            final String accessorName, final String value) {
        IDfPermit permit = new DfPermit();
        permit.setAccessorName(accessorName);
        permit.setPermitType(permitType);
        permit.setPermitValue(value);
        return permit;
    }

    public static List<IDfPermit> createDefault(final String accessorName) {
        List<IDfPermit> result = new ArrayList<>();
        result.add(newPermit(IDfPermit.DF_ACCESS_PERMIT, accessorName,
                IDfACL.DF_PERMIT_NONE_STR));
        result.add(newPermit(IDfPermit.DF_EXTENDED_PERMIT, accessorName,
                StringUtils.join(convertExtended(ExtendedPermitData.DEFAULT),
                        ",")));
        return result;
    }

    private static int asExtended(final int permitType) {
        switch (permitType) {
        case IDfPermit.DF_ACCESS_PERMIT:
            return IDfPermit.DF_EXTENDED_PERMIT;
        case IDfPermit.DF_ACCESS_RESTRICTION:
            return IDfPermit.DF_EXTENDED_RESTRICTION;
        default:
            return permitType;
        }
    }

    public static List<IDfPermit> createPermits(final Integer permitType,
            final String accessorName, final Integer accessPermit,
            final Integer xPermit, final String applicationPermit)
        throws DfException {
        List<IDfPermit> result = new ArrayList<>();
        switch (permitType) {
        case IDfPermit.DF_ACCESS_PERMIT:
        case IDfPermit.DF_ACCESS_RESTRICTION:
            result.add(newPermit(permitType, accessorName,
                    String.valueOf(accessPermit)));
            result.add(newPermit(asExtended(permitType), accessorName,
                    StringUtils.join(convertExtended(xPermit), ",")));
            break;
        case IDfPermit.DF_APPLICATION_PERMIT:
        case IDfPermit.DF_APPLICATION_RESTRICTION:
            result.add(newPermit(permitType, accessorName, applicationPermit));
            break;
        case IDfPermit.DF_REQUIRED_GROUP:
        case IDfPermit.DF_REQUIRED_GROUP_SET:
            result.add(newPermit(permitType, accessorName, ""));
            break;
        default:
            throw new DfException("Invalid permit type: " + permitType);
        }
        return result;
    }

}
