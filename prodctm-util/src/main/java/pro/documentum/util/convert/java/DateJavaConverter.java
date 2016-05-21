package pro.documentum.util.convert.java;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.documentum.fc.common.IDfTime;
import com.documentum.fc.common.IDfValue;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DateJavaConverter extends AbstractJavaConverter<Date> {

    public DateJavaConverter() {
        super();
    }

    @Override
    public List<Class<Date>> getJavaType() {
        return Collections.singletonList(Date.class);
    }

    @Override
    protected Date doConvert(final IDfValue value) throws ParseException {
        IDfTime time = value.asTime();
        if (time == null || time.isNullDate()) {
            return defaultValue();
        }
        return time.getDate();
    }

}
