package pro.documentum.util.convert.datastore;

import java.util.HashMap;
import java.util.Map;

import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfValue;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfValue;

import pro.documentum.util.convert.IConverter;
import pro.documentum.util.ids.DfIdUtil;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class IDfIdDataStoreConverter<F> extends
        AbstractDataStoreConverter<F, IDfId> {

    private static final Map<Class<?>, IConverter<?, IDfId>> CONVERTERS;

    static {
        CONVERTERS = new HashMap<>();
        CONVERTERS.put(String.class, new StringToIDfId());
        CONVERTERS.put(IDfValue.class, new IDfValueToIDfId());
        CONVERTERS.put(DfValue.class, CONVERTERS.get(IDfValue.class));
        CONVERTERS.put(IDfId.class, new IDfIdToIDfId());
        CONVERTERS.put(DfId.class, CONVERTERS.get(IDfId.class));
    }

    public IDfIdDataStoreConverter() {
        super();
    }

    @Override
    public int getDataStoreType() {
        return IDfValue.DF_ID;
    }

    @Override
    protected Map<Class<?>, IConverter<?, IDfId>> getConverters() {
        return CONVERTERS;
    }

    static class StringToIDfId implements IConverter<String, IDfId> {

        StringToIDfId() {
            super();
        }

        @Override
        public IDfId convert(final String obj) {
            return DfIdUtil.getId(obj);
        }

    }

    static class IDfIdToIDfId implements IConverter<IDfId, IDfId> {

        IDfIdToIDfId() {
            super();
        }

        @Override
        public IDfId convert(final IDfId value) {
            return value;
        }

    }

    static class IDfValueToIDfId implements IConverter<IDfValue, IDfId> {

        IDfValueToIDfId() {
            super();
        }

        @Override
        public IDfId convert(final IDfValue value) {
            return value.asId();
        }

    }

}
